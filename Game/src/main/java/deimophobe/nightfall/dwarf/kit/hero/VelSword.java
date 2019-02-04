package deimophobe.nightfall.dwarf.kit.hero;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.melee.AbstractRuneblade;

/**
 * Created by Deimophobe on 10/10/17.
 */
public class VelSword extends AbstractRuneblade {
	public VelSword(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type,15*20, ProcType.VELSWORD, ProcType.RUNEDASH);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("hero", "velsword");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getPickupType() { return PickupType.START;}
}
