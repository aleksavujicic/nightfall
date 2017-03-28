package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfShovel extends AbstractItem {
	
	public DwarfShovel(Dwarf dwarf) {super(dwarf);}
	
	private static final ItemStack ITEM = DwarfManager.getManager().getItem("misc.shovel");
	@Override public ItemStack getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.SHOVEL; }
}
