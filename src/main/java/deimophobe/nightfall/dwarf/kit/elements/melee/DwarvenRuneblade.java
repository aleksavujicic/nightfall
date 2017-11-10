package deimophobe.nightfall.dwarf.kit.elements.melee;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractItem;
import deimophobe.nightfall.items.CustomItem;

/**
 * Created by Deimophobe on 1/04/17.
 */
public class DwarvenRuneblade extends AbstractItem {
	
	public DwarvenRuneblade(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("melee", "drb");
	@Override public CustomItem getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
}
