package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Deimophobe on 18/01/17.
 */
public class Mob {
	
	protected final PlayerMonster monster;
	
	protected final String name;
	protected final String title;
	protected final boolean forceTitle;
	
	protected final DisguiseType disguiseType;
	protected final ItemStack helmet;
	protected final ItemStack chest;
	protected final List<ItemStack> items;
	protected final Collection<PotionEffect> effects;
	
	protected final boolean proccable;
	protected final double arrowRes;
	protected final int armourShred;
	protected final int torchXP;
	protected final boolean shrineImmune;
	
	public boolean isProccable() {
		return proccable;
	}
	
	public double getArrowRes() {
		return arrowRes;
	}
	
	public int getArmourShred() {
		return armourShred;
	}
	
	public boolean isShrineImmune() {
		return shrineImmune;
	}
	
	protected static final int POTION_LENGTH = 27*60*20;
	
	private Mob(PlayerMonster monster,
				String name, String title, boolean forceTitle,
				DisguiseType disguiseType,
				ItemStack helmet, ItemStack chest, List<ItemStack> items,
				Collection<PotionEffect> effects, int resLevel, int jumpLevel, boolean miningFatigue, int spawnImmunityTime,
				boolean proccable, double arrowRes, int armourShred, int torchXP, boolean shrineImmune) {
		this.monster = monster;
		
		this.name = name;
		this.title = title;
		this.forceTitle = forceTitle;
		
		this.disguiseType = disguiseType;
		
		this.helmet = helmet;
		this.chest = chest;
		if (items == null)
			items = new ArrayList<>();
		this.items = items;
		
		
		if (effects == null)
			effects = new HashSet<>();
		if (resLevel != 0) {
			effects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, POTION_LENGTH, resLevel - 1, true, false));
			effects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, POTION_LENGTH, 0, true, false));
		}
		if (jumpLevel != 0) {
			effects.add(new PotionEffect(PotionEffectType.JUMP, POTION_LENGTH, jumpLevel - 1, true, false));
		}
		if (miningFatigue) {
			effects.add(new PotionEffect(PotionEffectType.SLOW_DIGGING, POTION_LENGTH, 3, true, false));
		}
		if (spawnImmunityTime != 0) {
			effects.add(new PotionEffect(PotionEffectType.LUCK, spawnImmunityTime*20, 0));
		}
		this.effects = effects;
		
		this.proccable = proccable;
		this.arrowRes = arrowRes;
		this.armourShred = armourShred;
		this.torchXP = torchXP;
		this.shrineImmune = shrineImmune;
	}
	
	private static Mob createTemplateMob(ConfigurationSection section) {
		String name = section.getString("name");
		String title = section.getString("title");
		boolean forceTitle = section.getBoolean("forcetitle", false);
		
		DisguiseType type;
		if (section.contains("disguiseType")) {
			type = DisguiseType.valueOf(section.getString("disguiseType").toUpperCase());
		} else {
			type = null;
		}
		
		ItemStack helmet = ItemCreator.createItem(section.getConfigurationSection("helmet"), Slot.HEAD);
		ItemStack chest = ItemCreator.createItem(section.getConfigurationSection("chest"), Slot.CHEST);
		List<ItemStack> items = ItemCreator.createItems(section.getConfigurationSection("items"), Slot.MAIN_HAND);
		
		int resLevel = section.getInt("res", 3);
		int jumpLevel = section.getInt("jump", 0);
		boolean slowDig = section.getBoolean("slowdig", false);
		int immuneTime = section.getInt("immunetime", 8);
		
		boolean proccable = section.getBoolean("proccable", true);
		double arrowRes = section.getDouble("arrowres", 0);
		int armourShred = section.getInt("shred", 10);
		int torchXP = section.getInt("torchxp", 5);
		boolean shrineImmune = section.getBoolean("shrineimmune", false);
		
		return new Mob(null, name, title, forceTitle, type, helmet, chest, items, null, resLevel, jumpLevel, slowDig, immuneTime, proccable, arrowRes, armourShred, torchXP, shrineImmune);
	}
	
	protected Mob(Mob template, PlayerMonster monster) {
		this(monster,
			template.name, template.title, template.forceTitle,
			template.disguiseType,
			template.helmet, template.chest, template.items,
			template.effects, 0, 0, false, 0,
			template.proccable, template.arrowRes, template.armourShred, template.torchXP, template.shrineImmune);
	}
	
	public Mob clone(PlayerMonster monster) {
		return new Mob(this, monster);
	}
	
	
	public void spawn() {
		Player player = monster.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		if (forceTitle) {
			monster.setTitle(ChatColor.RED + title + ChatColor.RESET);
		} else {
			monster.setTitle(ChatColor.DARK_RED + title + " " + player.getName() + ChatColor.RESET);
		}
		
		monster.teleportTo(Game.getGame().getCurrentMobspawn());
		player.setGameMode(GameMode.SURVIVAL);
		
		if (disguiseType != null) {
			Disguise disguise = new MobDisguise(disguiseType);
			disguise = disguise.setViewSelfDisguise(false);
			disguise.getWatcher().setCustomNameVisible(false);
			disguise.getWatcher().setCustomName(ChatColor.DARK_RED + monster.getName());
			DisguiseAPI.disguiseEntity(player, disguise);
		}
		
		
		inv.clear();
		for (ItemStack item : items)
			inv.addItem(item);
		
		inv.setHelmet(helmet);
		inv.setChestplate(chest);
		monster.clearEffects();
		for (PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				monster.healPlayerMax();
			}
		}.runTaskLater(Game.getGame().getPlugin(), 20);
	}
	
	public void update() {}
	public void onShift(boolean sneaking) {}
	public void onUse(Action action, Block clickedBlock) {}
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	public float getCooldown() {
		return 0;
	}
	
	protected boolean isHoldingItem(int index) {
		return monster.getHeldItem().isSimilar(items.get(0));
	}
	
	private static final Map<MobType, Mob> templateMobs = new HashMap<>();
	static {
		ConfigurationSection mobs = MobManager.getManager().getMobConfig();
		
		templateMobs.put(MobType.WITHERSKELE, 	new WitherSkele(createTemplateMob(mobs.getConfigurationSection("witherskele")), null));
		templateMobs.put(MobType.FLAMELANCER,	new Flamelancer(createTemplateMob(mobs.getConfigurationSection("flamelancer")), null));
		templateMobs.put(MobType.WOLF, 			new Wolf(createTemplateMob(mobs.getConfigurationSection("wolf")), null));
		//templateMobs.put(MobType.DIRE_WOLF, 	new Wolf(createTemplateMob(mobs.getConfigurationSection("wolf")), null));
		templateMobs.put(MobType.SPIDERLING, 	new Spiderling(createTemplateMob(mobs.getConfigurationSection("spiderling")), null));
		templateMobs.put(MobType.RAT, 			new Rat(createTemplateMob(mobs.getConfigurationSection("rat")), null));
		templateMobs.put(MobType.GOLEM, 		new Golem(createTemplateMob(mobs.getConfigurationSection("golem")), null));
		templateMobs.put(MobType.OGRE, 			new Ogre(createTemplateMob(mobs.getConfigurationSection("ogre")), null));
		templateMobs.put(MobType.KRUNGOR, 		new Krungor(createTemplateMob(mobs.getConfigurationSection("krungor")), null));
	}
	
	public static Mob getTemplate(MobType type) {
		return templateMobs.get(type);
	}
}
