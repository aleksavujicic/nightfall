package deimophobe.nightfall.dwarf.kit.elements.healing;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 22/01/17.
 */
public class HealingAle extends AbstractAle {
	private final static int MANA_COST = 100;
	
	public HealingAle(Dwarf dwarf) {
		super(dwarf, MANA_COST);
	}
	
	private final static CustomItem ITEM = getAle("healing", MANA_COST);
	@Override public CustomItem getItem() { return ITEM; }
}
