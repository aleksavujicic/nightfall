package deimophobe.dvz.dwarf;

import deimophobe.dvz.items.modifiers.ItemModifier;
import deimophobe.dvz.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 5/05/17.
 */
public interface Armour {
	boolean isArmoured();
	void putOn();
	
	default void addModifier(ItemModifierType type, int value) { addModifier(type, value, null);}
	void addModifier(ItemModifierType type, int value, String reason);
	
	void setMax(int max);
	
	boolean isAtMax();
	
	void damage(int damage);
	void repair(int amount);
	
	double getResistance();
	int getManaRegenRate();
	
	enum Type {
		DWARF,
		HERO
		;
		
		Armour getArmour(Dwarf dwarf) {
			switch (this) {
				case DWARF: return new DwarvenArmour(dwarf);
				case HERO: return new HeroArmour();
			}
			throw new IllegalArgumentException("Unknown armour type " + this);
		}
	}
}
