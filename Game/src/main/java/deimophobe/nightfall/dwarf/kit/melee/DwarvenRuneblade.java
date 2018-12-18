package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.PickupType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;

/**
 * Created by Deimophobe on 1/04/17.
 */
public class DwarvenRuneblade extends AbstractItem {
	
	public DwarvenRuneblade(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("melee", "drb");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public PickupType getGiveType() { return PickupType.SWORD; }
}
