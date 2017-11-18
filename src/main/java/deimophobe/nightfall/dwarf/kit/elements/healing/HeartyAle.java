package deimophobe.nightfall.dwarf.kit.elements.healing;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.modifiers.ItemModifierType;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class HeartyAle extends AbstractAle {
	private final static int MANA_COST = 100;
	
	public HeartyAle(Dwarf dwarf) {
		super(dwarf, MANA_COST);
		dwarf.getArmour().addModifier(ItemModifierType.HEALTH, 8, "Hearty Ale");
	}
	
	private final static CustomItem ITEM = getAle("hearty", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
}
