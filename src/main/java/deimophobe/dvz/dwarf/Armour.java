package deimophobe.dvz.dwarf;

/**
 * Created by Deimophobe on 5/05/17.
 */
public interface Armour {
	boolean isArmoured();
	void putOn();
	
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
				case HERO: return new HeroArmour(dwarf);
			}
			throw new IllegalArgumentException("Unknown armour type " + this);
		}
	}
}
