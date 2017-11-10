package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 9/11/17.
 */
abstract class StaticArmour implements Armour {
	@Override public void addModifier(ItemModifierType type, int value, String reason) {}
	@Override public void updateEquipment() { }
	@Override public boolean isArmoured() { return true; }
	@Override public boolean canPickRepair() { return false; }
	@Override public boolean canShrineRepair() { return false; }
	@Override public void damage(double damage) {}
	@Override public void repair(double amount) {}
}
