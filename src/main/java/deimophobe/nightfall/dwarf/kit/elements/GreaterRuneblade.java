package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.items.CustomItem;

/**
 * Created by Deimophobe on 20/01/17.
 */
class GreaterRuneblade extends AbstractRuneblade {
	GreaterRuneblade(Dwarf dwarf) {
		super(dwarf, 20*20, ProcType.REGULAR, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("sword.grb");
	@Override public CustomItem getItem() {
		return ITEM;
	}
}
