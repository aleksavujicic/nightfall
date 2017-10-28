package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HeroArmour implements Armour {
	private final Hero hero;
	public HeroArmour(Hero hero) {
		this.hero = hero;
	}
	
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
	public boolean canRepair() {
		return false;
	}
	@Override
	public boolean isAtMax() {return true;}

	@Override
	public int getMaxArmor() {
		return 0;
	}
	
	@Override
	public int getValue() {
		return 1000;
	}
	
	@Override
	public void damage(int damage) {}
	
	@Override
	public void repair(int amount) {}

	
	@Override
	public double getResistance() {
		GameMap map = GameMap.getCurrentMap();
		Region shrine = map.getCurrentShrineRegion();
		
		if (shrine == null || !shrine.containsPlayer(hero)) return 0.8;
		
		int gold = map.getGold();
		return 0.8 + 0.05*(double) gold/500;
	}
	
	@Override
	public int getManaRegenRate() {
		return 15;
	}
}
