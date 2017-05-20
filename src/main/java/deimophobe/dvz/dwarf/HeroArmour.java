package deimophobe.dvz.dwarf;

import deimophobe.dvz.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HeroArmour implements Armour {
	HeroArmour() {}
	
	@Override
	public boolean isArmoured() {
		return true;
	}
	
	@Override
	public void putOn() {}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {}
	
	@Override
	public void increaseMax(int max) {}
	
	@Override
	public boolean isAtMax() {
		return true;
	}
	
	@Override
	public void damage(int damage) {}
	
	@Override
	public void repair(int amount) {}

	
	@Override
	public double getResistance() {
		return 0.84;
	}
	
	@Override
	public int getManaRegenRate() {
		return 15;
	}
}
