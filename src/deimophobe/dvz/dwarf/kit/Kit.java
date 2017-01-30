package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
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
	
	// TODO remove these
	public boolean hasRuneblessed()	{return armour == ArmourType.RUNEBLESSED;}
	public boolean hasQuiver() 		{return armour == ArmourType.QUIVER;}
	public boolean hasStudded()		{return armour == ArmourType.STUDDED;}
	public boolean hasCoil() 		{return armour == ArmourType.COIL;}
	
	// TODO?
	public boolean hasAndIsHoldingTM() {
		ItemStack item = dwarf.getHeldItem();
		return (sword.getType() == SwordType.TOMBMAKER && sword.matchesItem(item));
	}
	
	public boolean hasPassive(Passive passive) {
		return passives.contains(passive);
	}
	
	public Kit(Dwarf dwarf, Loadout loadout) {
		this.dwarf = dwarf;
		
		this.sword = Sword.createSword(dwarf, loadout.getSwordType());
		this.bow = Bow.createBow(dwarf, loadout.getBowType());
		this.ale = Ale.createAle(dwarf, loadout.getAleType());
		
		this.armour = loadout.getArmour();
		this.passives = loadout.getPassives();
	}
	
	
	//public Set<ItemStack> getItems() {
	//
	//}
	
	
	public void update() {
		sword.update();
		bow.update();
		ale.update();
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
	
	// TODO
	public double onHit(GameEntity monster, DamageType type) {
		ItemStack item = dwarf.getHeldItem();
		if (type.isMelee() && sword.matchesItem(item)) {
			sword.onHit(monster);
			return -1;
		} else if (type.isRanged()) {
			return bow.onHit(monster);
		}
		return -1;
	}
	
	public void onGotHit(GameEntity monster, DamageType type, double damage) {
		ale.onGotHit(monster, type, damage);
	}
	
	public void onKill(GameEntity monster, DamageType type) {
		ItemStack item = dwarf.getHeldItem();
		sword.onKill(monster, type.isMelee() && sword.matchesItem(item));
		bow.onKill(monster, type.isRanged() && bow.matchesItem(item));
		
		if (armour == ArmourType.QUIVER)
			dwarf.giveArrow();
	}
	
	public Projectile onBowFire(Arrow arrow, float force) {
		return bow.onBowFire(arrow, force);
	}
	
	public void onArrowLand(Arrow arrow, Block hitBlock) { bow.onArrowLand(arrow, hitBlock); }
	
	public void onShift(boolean sneaking) {
	}
	
	public float fractionComplete() {
		return sword.fractionComplete();
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
}
