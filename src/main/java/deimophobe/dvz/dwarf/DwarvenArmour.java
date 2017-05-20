package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.effects.GameEffect;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 5/05/17.
 */
public class DwarvenArmour implements Armour {
	private final Dwarf dwarf;
	
	private boolean armoured = false;
	
	private static final int DEFAULT_MAX = 2000;
	private int armour = DEFAULT_MAX;
	private int max = DEFAULT_MAX;
	
	private ArmourLevel currentLevel = ArmourLevel.HIGH;
	private Map<ArmourLevel, ArmourSet> setMap = new HashMap<>();
	
	public DwarvenArmour(Dwarf dwarf) {
		this.dwarf = dwarf;
		
		for (ArmourLevel level : ArmourLevel.values()) {
			ArmourSet set = level.getSet();
			//set.chest.addModifier(ItemModifierType.DURABILITY, DEFAULT_MAX);
			setMap.put(level, set);
		}
	}
	
	@Override
	public boolean isArmoured() {
		return armoured;
	}
	
	@Override
	public void putOn() {
		armoured = true;
		setMap.get(currentLevel).equip(dwarf);
		GameEffect.playEffect(GameEffect.DWARF_ARMOURED, dwarf.getPlayer());
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		for (ArmourSet set : setMap.values()) {
			set.chest.addModifier(type, value, reason);
		}
	}
	
	@Override
	public void increaseMax(int amt) {
		this.max += amt;
		this.armour += amt;
		updateArmour();
	}
	
	@Override
	public boolean isAtMax() {
		return armour == max;
	}
	
	private double armourFraction() {
		return (double) armour/max;
	}
	
	@Override
	public void damage(int damage) {
		if (Game.getGame().getPhase() == Phase.BUILD) return;
		
		armour -= damage;
		if (armour <= 0) armour = 0;
		updateArmour();
	}
	
	@Override
	public void repair(int amount) {
		armour += amount;
		if (armour >= max) armour = max;
		updateArmour();
	}
	
	
	@Override
	public double getResistance() {
		if (isArmoured()) {
			double x = armourFraction();
			return (0.15d/(1d + Math.exp(7d * (0.5d - x)))) + 0.7d;
		} else {
			return 0.6;
		}
	}
	
	@Override
	public int getManaRegenRate() {
		if (!isArmoured()) return 0;
		
		if (isAtMax()) return 15; // Otherwise formula below would give 16 only when full (which is kinda weird).
		return (int) Math.floor(Math.atan(3 * armourFraction()) * 16/Math.atan(3));
	}
	
	
	private void updateArmour() {
		if (isArmoured() && !currentLevel.isValid(this)) {
			currentLevel = ArmourLevel.getLevel(this);
			setMap.get(currentLevel).equip(dwarf);
		}
		
		dwarf.getPlayer().setFoodLevel((int) Math.ceil(20f * armourFraction()));
	}
	
	
	
	private enum ArmourLevel {
		HIGH("high", 0.7, 1),
		MED("med", 0.3, 0.7),
		LOW("low", 0, 0.3)
		;
		
		private final String setName;
		private final double minArmour;
		private final double maxArmour;
		ArmourLevel(String sectionName, double minArmour, double maxArmour) {
			this.setName = "armour." + sectionName;
			this.minArmour = minArmour;
			this.maxArmour = maxArmour;
		}
		
		private ArmourSet getSet() {
			return new ArmourSet(setName);
		}
		
		private boolean isValid(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			return  (minArmour <= frac && frac <= maxArmour);
		}
		
		private static ArmourLevel getLevel(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			for (ArmourLevel level : values()) {
				if (frac >= level.minArmour)
					return level;
			}
			return LOW;
		}
	}
	
	private static class ArmourSet {
		private final CustomItem chest;
		private final CustomItem legs;
		private final CustomItem boots;
		
		private ArmourSet(String section) {
			chest = DwarvenItems.getItem(section + ".chest", Slot.CHEST);
			legs = DwarvenItems.getItem(section + ".legs", Slot.LEGS);
			boots = DwarvenItems.getItem(section + ".boots", Slot.FEET);
		}
		
		private void equip(Dwarf dwarf) {
			PlayerInventory inv = dwarf.getPlayer().getInventory();
			inv.setChestplate(chest.createItemStack());
			inv.setLeggings(legs.createItemStack());
			inv.setBoots(boots.createItemStack());
		}
	}
}
