package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.util.ArmourSlot;

/**
 * Created by Deimophobe on 5/05/17.
 */
public interface Armour {
	default void addModifier(ItemModifierType type, int value) { addModifier(type, value, null);}
	void addModifier(ItemModifierType type, int value, String reason);
	void addModifier(ItemModifierType type, int value, String reason, ArmourSlot slot);
	void updateEquipment();
	
	double getFullness();
	boolean isArmoured();
	
	boolean canPickRepair();
	boolean canShrineRepair();
	void damage(double amount);
	void repair(double amount);
	
	double getResistance();
	int getManaRegenRate();
}
