package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.melee.AbstractRuneblade;

/**
 * Created by Deimophobe on 10/10/17.
 */
public class VelSword extends AbstractRuneblade {
	public VelSword(Dwarf dwarf) {
		super(dwarf, 20*20, ProcType.VELSWORD, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "velsword");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.START;}
}
