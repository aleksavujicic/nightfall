package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 5/05/17.
 */
public interface Armour {
	void addModifier(ItemModifierType type, int value, String reason);
	default void addModifier(ItemModifierType type, int value) { addModifier(type, value, null);}
	void updateEquipment();
	
	boolean isArmoured();
	
	boolean canPickRepair();
	boolean canShrineRepair();
	void damage(double amount);
	void repair(double amount);
	
	double getResistance();
	int getManaRegenRate();
}
