package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;

/**
 * Created by Deimophobe on 20/01/17.
 */
public class Runesword extends AbstractRuneblade {
	public Runesword(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type, 25*20, ProcType.REGULAR, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("melee", "runesword");
	@Override public CustomItem getItem() {
		return ITEM;
	}

}
