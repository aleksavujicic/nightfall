package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.loadout.DwarfData;
import deimophobe.dvz.dwarf.Dwarf;

import deimophobe.dvz.dwarf.kit.ale.Ale;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.bow.Bow;
import deimophobe.dvz.dwarf.kit.bow.BowType;
import deimophobe.dvz.dwarf.kit.consumable.Consumable;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.dwarf.kit.sword.Sword;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Kit {
	private final Dwarf dwarf;
	
	private final List<DwarvenItem> items;
	
	private final Sword sword;
	private final Bow bow;
	private final Ale ale;
	
	private final ArmourType armour;
	private final Collection<Passive> passives;
	
	private int shiftCD = 0;
	private final static int MAX_SHIFT_CD = 60*20;
	
	private boolean canPickup = true;
	
	public Kit(Dwarf dwarf, DwarfData dwarfData) {
		this.dwarf = dwarf;
		
		items = new ArrayList<>();
		
		this.sword = Sword.createSword(dwarf, dwarfData.getSwordType());
		this.bow = Bow.createBow(dwarf, dwarfData.getBowType());
		this.ale = Ale.createAle(dwarf, dwarfData.getAleType());
		items.add(sword);
		items.add(bow);
		items.add(ale);
		
		this.armour = dwarfData.getArmour();
		this.passives = dwarfData.getPassives();
		
		if (armour == ArmourType.STUDDED)
			dwarf.givePotionEffect(PotionEffectType.SLOW, 720000, -1, false, false, false);
	}
	
	public void disablePickup() {
		canPickup = false;
	}
	
	public void giveSword() {
		if (canPickup) dwarf.giveItem(sword.getItem());
	}
	public void giveBow() {
		if (canPickup) dwarf.giveItem(bow.getItem());
	}
	public void giveAle() {
		if (canPickup) dwarf.giveItem(ale.getItem());
	}
	public void giveAllItems() {
		if (canPickup)
			for (DwarvenItem item : items)
				dwarf.giveItem(item.getItem());
	}
	
	public int getMaxArmour() {
		return (armour == ArmourType.RUNEBLESSED ? 3000 : 2000);
	}
	
	public int getMaxArrows() {
		return (armour == ArmourType.QUIVER ? 40 : 20);
	}
	
	public boolean hasPassive(Passive passive) {
		return passives.contains(passive);
	}
	
	
	// ------ EVENTS ------
	public void update() {
		for (DwarvenItem item : items)
			item.update();
		
		if (shiftCD > 0)
			shiftCD--;
	}
	
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		ItemStack held = dwarf.getHeldItem();
		for (DwarvenItem item : items) {
			if (item.matchesItem(held)) {
				item.onUse(action, clickedBlock, blockFace);
				return true;
			}
		}
		return false;
	}
	
	public double onHit(GameEntity monster, DamageType type, double damage) {
		ItemStack item = dwarf.getHeldItem();
		if (type.isMelee() && sword.matchesItem(item)) {
			return sword.onHit(monster, type, damage);
		} else if (type.isRanged()) {
			return bow.onHit(monster, type, damage);
		}
		return damage;
	}
	
	public void onGotHit(GameEntity monster, DamageType type, double damage) {
		for (DwarvenItem item : items)
			item.onGotHit(monster, type, damage);
	}
	
	public void onKill(GameEntity monster, DamageType type) {
		sword.onKill(monster, type);
		bow.onKill(monster, type);
		
		if (armour == ArmourType.QUIVER)
			dwarf.giveArrow();
	}
	
	public void onBlockBreak(Block block) {
		ItemStack held = dwarf.getHeldItem();
		for (DwarvenItem item : items)
			if (item.matchesItem(held))
				item.onBlockBreak(block);
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		return bow.onBowFire(arrow, force);
	}
	
	public void onProjectileLand(Projectile proj, Block hitBlock) { bow.onProjectileLand(proj, hitBlock); }
	
	public void onShift(boolean sneaking) {
		if (shiftCD == 0 && !sneaking) {
			shiftCD = MAX_SHIFT_CD;
			if (passives.contains(Passive.DARKVISION)) {
				dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 200, 1, true, true, true);
				dwarf.updateVisibility();
			}
			if (passives.contains(Passive.SAFEFALL))
				dwarf.givePotionEffect(PotionEffectType.JUMP, 200, 3, true, true, true);
		}
	}
	
	// ------ MISC ------
	public static boolean isDroppableItem(ItemStack item) {
		if (item == null) return true;
		
		for (SwordType swordType : SwordType.values())
			if (item.isSimilar(Sword.getItem(swordType)))
				return false;
		
		for (BowType bowType : BowType.values())
			if (item.isSimilar(Bow.getItem(bowType)))
				return false;
		
		for (AleType aleType : AleType.values())
			if (item.isSimilar(Ale.getItem(aleType)))
				return false;
		
		for (ConsumableType consumableType : Consumable.undroppableConsumables)
			if (item.isSimilar(Consumable.getItem(consumableType)))
				return false;
		
		return true;
	}
	
	public boolean isBlindnessImmune() {
		return (bow.getBowType() == BowType.LIGHTBOW);
	}
	
	
	// ------ HELD ITEM ------
	private DwarvenItem lastHeld = null;
	public float fractionComplete() {
		if (lastHeld == null) return 0;
		return lastHeld.fractionComplete();
	}
	
	public void updateHotbarSlot(ItemStack newItem) {
		if (newItem == null) return;
		for (DwarvenItem item : items) {
			if (item.matchesItem(newItem)) {
				if (item.fractionComplete() != -1)
					lastHeld = item;
				
				return;
			}
		}
	}
}
