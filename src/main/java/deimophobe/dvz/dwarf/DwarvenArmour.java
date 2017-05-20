package deimophobe.dvz.dwarf;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.effects.GameEffect;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
	
	public DwarvenArmour(Dwarf dwarf) {
		this.dwarf = dwarf;
	}
	
	@Override
	public boolean isArmoured() {
		return armoured;
	}
	
	@Override
	public void putOn() {
		armoured = true;
		currentLevel.equip(dwarf);
		GameEffect.playEffect(GameEffect.DWARF_ARMOURED, dwarf);
	}
	
	@Override
	public void setMax(int max) {
		this.max = max;
		this.armour = max;
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
			currentLevel.equip(dwarf);
		}
		
		dwarf.getPlayer().setFoodLevel((int) Math.ceil(20f * armourFraction()));
	}
	
	
	
	private enum ArmourLevel {
		HIGH("high", 0.7, 1),
		MED("med", 0.3, 0.7),
		LOW("low", 0, 0.3)
		;
		
		private final ArmourSet set;
		private final double minArmour;
		private final double maxArmour;
		ArmourLevel(String sectionName, double minArmour, double maxArmour) {
			this.set = new ArmourSet("armour." + sectionName);
			this.minArmour = minArmour;
			this.maxArmour = maxArmour;
		}
		
		private boolean isValid(DwarvenArmour armour) {
			double frac = armour.armourFraction();
			return  (minArmour <= frac && frac <= maxArmour);
		}
		
		private void equip(Dwarf dwarf) {
			set.equip(dwarf);
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
		private final ItemStack chest;
		private final ItemStack legs;
		private final ItemStack boots;
		
		private ArmourSet(String section) {
			chest = DwarvenItems.createItemStack(section + ".chest", Slot.CHEST);
			legs = DwarvenItems.createItemStack(section + ".legs", Slot.LEGS);
			boots = DwarvenItems.createItemStack(section + ".boots", Slot.FEET);
		}
		
		private void equip(Dwarf dwarf) {
			PlayerInventory inv = dwarf.getPlayer().getInventory();
			inv.setChestplate(chest);
			inv.setLeggings(legs);
			inv.setBoots(boots);
		}
	}
}
