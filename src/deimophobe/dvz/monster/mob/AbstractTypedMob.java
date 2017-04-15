package deimophobe.dvz.monster.mob;

import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.Skin;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.upgrade.GlobalUpgrade;
import deimophobe.dvz.shrine.ShrineManager;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import minecraft.spigot.community.michel_0.api.Attribute;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Deimophobe on 18/01/17.
 */
abstract class AbstractTypedMob extends AbstractMob {
	
	private List<ItemStack> items;
	
	private final MobData mobData;
	
	
	
	protected AbstractTypedMob(MonsterPlayer monster) {
		super(monster);
		this.mobData = getType().getMobData();
	}
	
	protected AbstractTypedMob(MonsterPlayer monster, MobType type) {
		super(monster);
		this.mobData = type.getMobData();
	}
	
	@Override
	public void spawn() {
		setTitle(mobData.forceTitle, mobData.title);
		giveItems();
		
		DisguiseType type = mobData.disguiseType;
		if (type != null) {
			if (type == DisguiseType.PLAYER) {
				setupPlayerDisguise(Skin.getSkin(mobData.skinName), mobData.playerName);
			} else {
				setupMobDisguise(type);
			}
		}
		
		
		monster.clearEffects();
		givePermanentPotionEffect(PotionEffectType.NIGHT_VISION, 1);
		if (mobData.immuneTime != 0) {
			monster.givePotionEffect(PotionEffectType.LUCK, mobData.immuneTime*20, 0, true, false, true);
		}
		
		monster.teleportTo(ShrineManager.getManager().getCurrentMobspawn());
		monster.getPlayer().setGameMode(GameMode.SURVIVAL);
	}
	
	protected void giveItems() {
		PlayerInventory inv = monster.getPlayer().getInventory();
		//MobUpgrades upgrades = monster.getUpgrades(type);
		
		monster.clearInventory();
		
		//int attack = mobData.attack + upgrades.getUpgrade("attack");
		//int health = mobData.health + upgrades.getUpgrade("health");
		//int speed = mobData.speed + upgrades.getUpgrade("speed");
		
		int attack = mobData.attack;
		int health = mobData.health;
		int speed = mobData.speed;
		
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
	
	@Override public boolean isProccable() {
		return mobData.proccable;
	}
	@Override public double getResistance() {
		return mobData.damageRes;
	}
	@Override public double getArrowRes() {
		return mobData.arrowRes;
	}
	@Override public int getArmourShred() {
		return mobData.armourShred;
	}
	@Override public int getTorchXP() {
		return mobData.torchXP;
	}
	@Override public boolean isShrineImmune() {
		return mobData.shrineImmune;
	}
	
	protected abstract MobType getType();
}
