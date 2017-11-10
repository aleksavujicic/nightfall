package deimophobe.nightfall.dwarf.armour;

import deimophobe.nightfall.ArmourSlot;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.hero.Hero;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class HeroArmour extends StaticArmour {
	private final Hero hero;
	private final ArmourSlot slot;
	private final CustomItem armour;
	
	public HeroArmour(Hero hero, String hatName) {
		this(hero, hatName, ArmourSlot.HEAD);
	}
	
	public HeroArmour(Hero hero, String hatName, ArmourSlot slot) {
		this.hero = hero;
		this.slot = slot;
		this.armour = DwarvenItems.getItem("hero-hat", hatName, slot.getSlot());
		updateEquipment();
	}
	
	@Override
	public void addModifier(ItemModifierType type, int value, String reason) {
		armour.addModifier(type, value, reason);
		updateEquipment();
	}
	
	@Override
	public void updateEquipment() {
		slot.equipArmour(hero, armour);
	}
	
	@Override
	public double getResistance() {
		GameMap map = GameMap.getCurrentMap();
		Region shrine = map.getCurrentShrineRegion();
		
		double goldboost = 0;
		if (shrine != null && shrine.containsPlayer(hero))
			goldboost = 0.02;
		
		double dwarfBoost = 0.01 * Math.sqrt(DwarfManager.getManager().getNumberOfPlayers());
		return Math.min(0.8 + goldboost + dwarfBoost,0.9);
	}
	
	@Override
	public int getManaRegenRate() {
		return 15;
	}
}
