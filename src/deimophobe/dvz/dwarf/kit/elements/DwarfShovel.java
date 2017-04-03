package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfShovel extends AbstractItem {
	DwarfShovel(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final ItemStack ITEM = DwarvenItems.getItem("misc.shovel");
	@Override public ItemStack getItem() {return ITEM;}
	
	
	@Override
	public KitGiveType getGiveType() {
		if (dwarf.hasKitElement(KitElementType.TOMBMAKER))
			return null;
		else
			return KitGiveType.SHOVEL;
	}
}
