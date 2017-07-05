package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfShovel extends AbstractItem {
	DwarfShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final CustomItem ITEM = DwarvenItems.getItem("misc.shovel");
	@Override public CustomItem getItem() {return ITEM;}
	
	
	@Override
	public KitGiveType getGiveType() {
		if (dwarf.hasKitElement(KitElementType.TOMBMAKER))
			return null;
		else
			return KitGiveType.SHOVEL;
	}
}
