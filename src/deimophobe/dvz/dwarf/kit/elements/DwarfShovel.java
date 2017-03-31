package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfShovel extends AbstractItem {
	
	private final boolean hasTombmaker;
	
	public DwarfShovel(Dwarf dwarf) {
		super(dwarf);
		hasTombmaker = dwarf.hasKitElement(KitElementType.TOMBMAKER);
	}
	
	private static final ItemStack ITEM = DwarvenItems.getItem("misc.shovel");
	@Override public ItemStack getItem() {return ITEM;}
	
	@Override
	public KitGiveType getGiveType() {
		if (hasTombmaker)
			return null;
		else
			return KitGiveType.SHOVEL;
	}
}
