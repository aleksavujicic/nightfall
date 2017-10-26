package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.effects.GameEffect;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 5/05/17.
 */
public class DwarvenArmour implements Armour {
	private final Dwarf dwarf;
	
	private boolean armoured = false;
	
	private static final int DEFAULT_MAX = 1000;
	private int armour = DEFAULT_MAX;
	private int max = DEFAULT_MAX;
	
	private ArmourLevel currentLevel = ArmourLevel.SHINY;
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
		if (!armoured) {
			armoured = true;
			setMap.get(currentLevel).equip(dwarf);
			GameEffect.playEffect(GameEffect.DWARF_ARMOURED, dwarf.getPlayer());
			dwarf.onArmourEquip();
		} else {
			Bukkit.getLogger().severe("Tried to equip armour on dwarf which is already equipped!\nDwarf: " + dwarf.getName());
		}
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
	public int getMaxArmor() {
		return max;
	}
	
	@Override
	public int getValue() {
		return armour;
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
			return (x * 0.125 + 0.7);
		} else {
			return 0.6;
		}
	}
	
	@Override
	public int getManaRegenRate() {
		if (!isArmoured()) return 0;
		
		if (isAtMax()) return 10; // Otherwise formula below would give 11 only when full (which is kinda weird).
		return (int) Math.floor(Math.atan(2 * armourFraction()) * 10/Math.atan(2)) + 1;
	}
	
	
	private void updateArmour() {
		if (isArmoured() && !currentLevel.isValid(this)) {
			currentLevel = ArmourLevel.getLevel(this);
			setMap.get(currentLevel).equip(dwarf);
		}
		
		dwarf.getPlayer().setFoodLevel((int) Math.ceil(20f * armourFraction()));
	}
	
	
	
	private enum ArmourLevel {
		SHINY("shiny", 0.8, 1),
		HIGH("high", 0.6, 0.8),
		MED("med", 0.3, 0.6),
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
