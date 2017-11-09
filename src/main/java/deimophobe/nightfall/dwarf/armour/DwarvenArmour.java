package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
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
	
	private static final double DEFAULT_MAX = 1000;
	private double armourValue = DEFAULT_MAX;
	private double durability = 100;
	
	private ArmourLevel currentLevel = ArmourLevel.SHINY;
	private Map<ArmourLevel, ArmourSet> setMap = new HashMap<>();
	
	public DwarvenArmour(Dwarf dwarf) {
		this.dwarf = dwarf;
		
		for (ArmourLevel level : ArmourLevel.values()) {
			ArmourSet set = level.getSet();
			setMap.put(level, set);
		}
		addModifier(ItemModifierType.QUIVER, 20);
	}
	
	@Override
	public boolean isArmoured() { return armoured; }
	public void putOn() {
		if (!armoured) {
			armoured = true;
			setMap.get(currentLevel).equip(dwarf);
			GameEffect.DWARF_ARMOURED.playEffect(dwarf);
			dwarf.onArmourEquip();
		} else {
			Bukkit.getLogger().warning("Tried to equip armour on dwarf which is already equipped!\nDwarf: " + dwarf.getName());
		}
	}
	
	
	public double getValue() {
		return armourValue;
	}
	public void changeDurability(double amt, String reason) {
		addModifier(ItemModifierType.ARMOUR_DURABILITY, (int) amt, reason);
		durability += amt;
	}
	
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		for (ArmourSet set : setMap.values()) {
			set.chest.addModifier(type, value, reason);
		}
		updateArmour(true);
	}
	@Override
	public void updateEquipment() {
		updateArmour(true);
	}
	
	
	@Override
	public boolean canPickRepair() {
		return armourFraction() >= 0.35;
	}
	@Override
	public boolean canShrineRepair() {
		return isAtMax();
	}
	
	@Override
	public void damage(double damage) {
		if (Game.getGame().getPhase() == Phase.BUILD) return;
		
		armourValue -= damage/(durability/100);
		if (armourValue <= 0) armourValue = 0;
		updateArmour();
	}
	@Override
	public void repair(double amount) {
		armourValue += amount;
		if (armourValue >= DEFAULT_MAX) armourValue = DEFAULT_MAX;
		updateArmour();
	}
	
	
	@Override
	public double getResistance() {
		if (isArmoured()) {
			double x = armourFraction();
			int n = DwarfManager.getManager().getNumberOfPlayers();
			return (x * 0.125 + 0.7 + 0.06d/n);
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
	
	
	private boolean isAtMax() { return armourFraction() >= 1; }
	private double armourFraction() { return armourValue/DEFAULT_MAX; }
	private void updateArmour() { updateArmour(false);	}
	private void updateArmour(boolean force) {
		if (isArmoured() && (force ||!currentLevel.isValid(this))) {
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
			this.setName = sectionName;
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
			chest = DwarvenItems.getItem("armour", section + ".chest", Slot.CHEST);
			legs = DwarvenItems.getItem("armour", section + ".legs", Slot.LEGS);
			boots = DwarvenItems.getItem("armour", section + ".boots", Slot.FEET);
		}
		
		private void equip(Dwarf dwarf) {
			PlayerInventory inv = dwarf.getPlayer().getInventory();
			inv.setChestplate(chest.createItemStack());
			inv.setLeggings(legs.createItemStack());
			inv.setBoots(boots.createItemStack());
		}
	}
}
