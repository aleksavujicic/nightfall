package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;

/**
 * Created by Deimophobe on 10/10/17.
 */
class VelSword extends AbstractRuneblade{
	VelSword(Dwarf dwarf) {
		super(dwarf, 15*20, ProcType.VELSWORD, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero.velsword");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.START;}
}
