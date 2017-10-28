package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 5/05/17.
 */
public interface Armour {
	boolean isArmoured();
	void putOn();
	
	default void addModifier(ItemModifierType type, int value) { addModifier(type, value, null);}
	void addModifier(ItemModifierType type, int value, String reason);
	
	void increaseMax(int amt);
	
	boolean isAtMax();
	boolean canRepair();
	
	void damage(int damage);
	void repair(int amount);
	
	double getResistance();
	int getManaRegenRate();
	int getMaxArmor();
	
	int getValue();
}
