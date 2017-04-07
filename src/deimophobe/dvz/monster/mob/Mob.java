package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.Skin;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
import deimophobe.dvz.monster.upgrade.MobUpgrades;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Deimophobe on 18/01/17.
 */
public abstract class Mob {
	
	protected final MonsterPlayer monster;
	protected final MobType type;
	
	private List<ItemStack> items;
	
	protected boolean proccable;
	protected double resistance;
	protected double arrowRes;
	protected int armourShred;
	protected int torchXP;
	protected boolean shrineImmune;
	
	
	private static final int POTION_LENGTH = 27*60*20;
	
	protected Mob(MonsterPlayer mons, MobType type) {
		this.monster = mons;
		this.type = type;
		
		MobData mobData = MobData.getMobData(type);
		
		proccable = mobData.proccable;
		resistance = mobData.damageRes;
		arrowRes = mobData.arrowRes;
		armourShred = mobData.armourShred;
		torchXP = mobData.torchXP;
		shrineImmune = mobData.shrineImmune;
		
		setTitle();
		setupDisguise();
		giveItems();
		givePotionEffects();
		
		spawn();
	}
	
	protected void spawn() {
		Player player = monster.getPlayer();
		
		monster.teleportTo(ShrineManager.getManager().getCurrentMobspawn());
		player.setGameMode(GameMode.SURVIVAL);
	}
	
	protected void setupDisguise() {
		MobData mobData = MobData.getMobData(type);
		Player player = monster.getPlayer();
		
		if (mobData.disguiseType != null) {
			if (mobData.disguiseType == DisguiseType.PLAYER) {
				PlayerDisguise disguise = Skin.getDisguiseWithSkin(mobData.skinName, mobData.playerName);
				disguise.setDisplayedInTab(true);
				disguise = disguise.setViewSelfDisguise(false);
				disguise.getWatcher().setCustomNameVisible(false);
				disguise.getWatcher().setCustomName(mobData.playerName);
				MonsterManager.getManager().addToTeam(mobData.playerName);
				DisguiseAPI.disguiseEntity(player, disguise);
			} else {
				Disguise disguise = new MobDisguise(mobData.disguiseType);
				disguise.getWatcher().setCustomNameVisible(false);
				disguise.getWatcher().setCustomName(monster.getDisplayName());
				disguise = disguise.setViewSelfDisguise(false);
				DisguiseAPI.disguiseEntity(player, disguise);
			}
		}
	}
	
	protected void setTitle() {
		MobData mobData = MobData.getMobData(type);
		
		ChatColor titleColor;
		if (mobData.forceTitle)
			titleColor = ChatColor.RED;
		else
			titleColor = ChatColor.DARK_RED;
		
		monster.setTitle(titleColor, mobData.title, mobData.forceTitle);
	}
	
	protected void givePotionEffects() {
		MobData mobData = MobData.getMobData(type);
		
		monster.clearEffects();
		monster.givePotionEffect(PotionEffectType.NIGHT_VISION, POTION_LENGTH, 1, false, false, true);
		if (mobData.jumpLevel != 0) {
			monster.givePotionEffect(PotionEffectType.JUMP, POTION_LENGTH, mobData.jumpLevel, false, false, true);
		}
		if (mobData.slowDig) {
			monster.givePotionEffect(PotionEffectType.SLOW_DIGGING, POTION_LENGTH, 4, false, false, true);
		}
		if (mobData.invisible) {
			monster.givePotionEffect(PotionEffectType.INVISIBILITY, POTION_LENGTH, 1, true, true, true);
		}
		if (mobData.immuneTime != 0) {
			monster.givePotionEffect(PotionEffectType.LUCK, mobData.immuneTime*20, 0, true, true, true);
		}
	}
	
	protected void giveItems() {
		MobData mobData = MobData.getMobData(type);
		PlayerInventory inv = monster.getPlayer().getInventory();
		MobUpgrades upgrades = monster.getUpgrades(type);
		
		monster.clearInventory();
		
		int attack = mobData.attack + upgrades.getUpgrade("attack");
		int health = mobData.health + upgrades.getUpgrade("health");
		int speed = mobData.speed + upgrades.getUpgrade("speed");
		
		if (GlobalUpgrade.KRUNGOR.isUnlocked()) {
			attack += 10;
		}
		
		
		// Add weapon
		items = new ArrayList<>();
		ItemStack weapon = ItemCreator.setAttribute(mobData.weapon, Attribute.ATTACK_DAMAGE, attack , Slot.MAIN_HAND);
		
		inv.addItem(weapon);
		items.add(weapon);
		
		// Add other items
		for (ItemStack item : mobData.items) {
			inv.addItem(item);
			items.add(item);
		}
		
		// Add armour
		Slot slot = (mobData.armourOnChest ? Slot.CHEST : Slot.HEAD);
		ItemStack armour = ItemCreator.setAttribute(mobData.armour, Attribute.MAX_HEALTH, health, slot);
		armour = ItemCreator.setAttribute(armour, Attribute.MOVEMENT_SPEED, speed, slot);
		if (mobData.armourOnChest) {
			inv.setChestplate(armour);
		} else {
			inv.setHelmet(armour);
		}
		monster.delayedHealMax();
	}
	
	
	protected boolean isPlayerHoldingItem(int index) {
		return monster.getHeldItem().isSimilar(items.get(index));
	}
	
	public Disguise getDisguise() {
		return DisguiseAPI.getDisguise(monster.getPlayer());
	}
	
	public boolean isProccable() {
		return proccable;
	}
	public double getResistance() {
		return resistance;
	}
	public double getArrowRes() {
		return arrowRes;
	}
	public int getArmourShred() {
		return armourShred;
	}
	public int getTorchXP() {
		return torchXP;
	}
	public boolean isShrineImmune() {
		return shrineImmune;
	}
	
	public void update() {}
	public void onShift(boolean sneaking) {}
	public void onBlockBreak(Block block) {}
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {}
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage;
	}
	public Projectile onBowFire(Arrow arrow, float force) {
		return null;
	}
	public void onProjectileLand(Projectile proj, Block hitBlock) {}
	public float getCooldown() {
		return 0;
	}
	public void onDeath() {}
	
	
	
	public static Mob createAndSpawnMob(MonsterPlayer monster, MobType type) {
		switch (type) {
			case ZOMBIE: return new Zombie(monster);
			case GOBO: return new Goblin(monster);
			case WITHERSKELE: return new WitherSkele(monster);
			case FLAMELANCER: return new Flamelancer(monster);
			case WOLF: return new Wolf(monster, false);
			case DIREWOLF: return new Wolf(monster, true);
			case SPIDERLING: return new Spiderling(monster);
			case SWAMMIE:
				break;
			case RAT: return new Rat(monster);
			case GOLEM: return new Golem(monster);
			case OGRE: return new Ogre(monster);
			case KRUNGOR: return new Krungor(monster);
			case BOPEN: return new Bopen(monster);
			
			case GB_DAGGER:
			case GB_RUNEBLADE:
			case GB_AXE:
			case GB_HAMMER:
				return new Ghostblade(monster, type);
			
			
			case PLAGUE_ZOMBIE: return new PlaguedZombie(monster);
		}
		Bukkit.getLogger().warning("Unknown mobtype: " + type);
		return null;
	}
	
}
