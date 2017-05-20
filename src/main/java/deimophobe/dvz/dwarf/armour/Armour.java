package deimophobe.dvz.dwarf.armour;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.items.modifiers.ItemModifierType;

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
	
	void damage(int damage);
	void repair(int amount);
	
	double getResistance();
	int getManaRegenRate();
	
	enum Type {
		DWARF,
		HERO
		;
		
		public Armour getArmour(Dwarf dwarf) {
			switch (this) {
				case DWARF: return new DwarvenArmour(dwarf);
				case HERO: return new HeroArmour();
			}
			throw new IllegalArgumentException("Unknown armour type " + this);
		}
	}
}
