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
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

/**
 * Created by Deimophobe on 16/01/17.
 */
public class Kit {
	private final Dwarf dwarf;
	
	private final Sword sword;
	private final Bow bow;
	private final Ale ale;
	
	private final ArmourType armour;
	private final Collection<Passive> passives;
	
	private int shiftCD = 0;
	private final static int MAX_SHIFT_CD = 60*20;
	
	
	// TODO?
	public boolean hasAndIsHoldingTM() {
		ItemStack item = dwarf.getHeldItem();
		return (sword.getType() == SwordType.TOMBMAKER && sword.matchesItem(item));
	}
	
	public boolean hasPassive(Passive passive) {
		return passives.contains(passive);
	}
	
	public Kit(Dwarf dwarf, DwarfData dwarfData) {
		this.dwarf = dwarf;
		
		this.sword = Sword.createSword(dwarf, dwarfData.getSwordType());
		this.bow = Bow.createBow(dwarf, dwarfData.getBowType());
		this.ale = Ale.createAle(dwarf, dwarfData.getAleType());
		
		this.armour = dwarfData.getArmour();
		this.passives = dwarfData.getPassives();
		
		if (armour == ArmourType.STUDDED)
			dwarf.givePotionEffect(PotionEffectType.SLOW, 720000, -1, false, false, false);
	}
	
	public int getMaxArmour() {
		return (armour == ArmourType.RUNEBLESSED ? 3000 : 2000);
	}
	
	public int getMaxArrows() {
		return (armour == ArmourType.QUIVER ? 40 : 20);
	}
	
	public void update() {
		sword.update();
		bow.update();
		ale.update();
		
		if (shiftCD > 0)
			shiftCD--;
	}
	
	public boolean use(Action type) {
		ItemStack item = dwarf.getHeldItem();
		if (sword.matchesItem(item)) {
			return sword.use(type);
		} else if (bow.matchesItem(item)) {
			return bow.use(type);
		} else if (ale.matchesItem(item)) {
			return ale.use(type);
		}
		
		return false;
	}
	
	public double onHit(GameEntity monster, DamageType type, double damage) {
		ItemStack item = dwarf.getHeldItem();
		if (type.isMelee() && sword.matchesItem(item)) {
			return sword.onHit(monster, damage);
		} else if (type.isRanged()) {
			return bow.onHit(monster, damage);
		}
		return damage;
	}
	
	public void onGotHit(GameEntity monster, DamageType type, double damage) {
		ale.onGotHit(monster, type, damage);
	}
	
	public void onKill(GameEntity monster, DamageType type) {
		sword.onKill(monster, type);
		bow.onKill(monster, type);
		
		if (armour == ArmourType.QUIVER)
			dwarf.giveArrow();
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		return bow.onBowFire(arrow, force);
	}
	
	public void onProjectileLand(Projectile proj, Block hitBlock) { bow.onProjectileLand(proj, hitBlock); }
	
	public void onShift(boolean sneaking) {
		if (shiftCD == 0) {
			shiftCD = MAX_SHIFT_CD;
			if (passives.contains(Passive.DARKVISION)) {
				dwarf.givePotionEffect(PotionEffectType.NIGHT_VISION, 200, 1, true, true, true);
				dwarf.updateVisibility();
			}
			if (passives.contains(Passive.SAFEFALL))
				dwarf.givePotionEffect(PotionEffectType.JUMP, 200, 3, true, true, true);
		}
	}
	
	public ItemStack getSwordItem() {
		return sword.getItem();
	}
	public ItemStack getBowItem() {
		return bow.getItem();
	}
	public ItemStack getHealItem() {
		return ale.getItem();
	}
	
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
	
	
	private HoldType lastHeld = HoldType.SWORD;
	public float fractionComplete() {
		switch (lastHeld) {
			case SWORD:
				return sword.fractionComplete();
			case BOW:
				return bow.fractionComplete();
			case ALE:
				return ale.fractionComplete();
		}
		throw new UnsupportedOperationException();
	}
	
	public void updateHotbarSlot(ItemStack heldItem) {
		if (heldItem == null) return;
		
		if (sword.matchesItem(heldItem) && sword.fractionComplete() != -1) {
			lastHeld = HoldType.SWORD;
		} else if (bow.matchesItem(heldItem) && bow.fractionComplete() != -1) {
			lastHeld = HoldType.BOW;
		} else if (ale.matchesItem(heldItem) && ale.fractionComplete() != -1) {
			lastHeld = HoldType.ALE;
		}
	}
	
	private enum HoldType {
		SWORD,
		BOW,
		ALE
	}
}
