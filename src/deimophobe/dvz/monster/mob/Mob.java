package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Deimophobe on 18/01/17.
 */
public class Mob {
	
	protected final PlayerMonster monster;
	
	private final List<ItemStack> items;
	
	private final boolean proccable;
	private final double arrowRes;
	private final int armourShred;
	private final int torchXP;
	private final boolean shrineImmune;
	
	protected Mob(PlayerMonster mons, MobType type) {
		monster = mons;
		
		MobData mobData = MobData.getMobData(type);
		
		Player player = monster.getPlayer();
		PlayerInventory inv = player.getInventory();
		
		if (mobData.forceTitle) {
			monster.setTitle(ChatColor.RED + mobData.title + ChatColor.RESET);
		} else {
			monster.setTitle(ChatColor.DARK_RED + mobData.title + " " + player.getName() + ChatColor.RESET);
		}
		
		monster.teleportTo(Game.getGame().getCurrentMobspawn());
		player.setGameMode(GameMode.SURVIVAL);
		
		if (mobData.disguiseType != null) {
			Disguise disguise = new MobDisguise(mobData.disguiseType);
			disguise = disguise.setViewSelfDisguise(false);
			disguise.getWatcher().setCustomNameVisible(false);
			disguise.getWatcher().setCustomName(ChatColor.DARK_RED + monster.getName());
			DisguiseAPI.disguiseEntity(player, disguise);
		}
		
		
		monster.clearInventory();
		for (ItemStack item : mobData.items)
			monster.giveItem(item);
		
		inv.setHelmet(mobData.helmet);
		inv.setChestplate(mobData.chest);
		
		
		monster.clearEffects();
		for (PotionEffect effect : mobData.effects) {
			player.addPotionEffect(effect);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				monster.healPlayerMax();
			}
		}.runTaskLater(Game.getGame().getPlugin(), 20);
		
		items = mobData.items;
		
		proccable = mobData.proccable;
		arrowRes = mobData.arrowRes;
		armourShred = mobData.armourShred;
		torchXP = mobData.torchXP;
		shrineImmune = mobData.shrineImmune;
	}
	
	protected boolean isPlayerHoldingItem(int index) {
		return monster.getHeldItem().isSimilar(items.get(0));
	}
	
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
	
	
	
	public static Mob createAndSpawnMob(PlayerMonster monster, MobType type) {
		switch (type) {
			case ZOMBIE:
				break;
			case WITHERSKELE:
				return new WitherSkele(monster);
			case FLAMELANCER:
				return new Flamelancer(monster);
			case WOLF:
				return new Wolf(monster);
			case SPIDERLING:
				return new Spiderling(monster);
			case SWAMMIE:
				break;
			case RAT:
				return new Rat(monster);
			case GOLEM:
				return new Golem(monster);
			case OGRE:
				return new Ogre(monster);
			case KRUNGOR:
				return new Krungor(monster);
		}
		Bukkit.getLogger().warning("Unknown mobtype: " + type);
		return null;
	}
}
