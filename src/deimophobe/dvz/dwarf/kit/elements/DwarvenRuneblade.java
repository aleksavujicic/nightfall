package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 1/04/17.
 */
class DwarvenRuneblade extends AbstractItem {
	
	DwarvenRuneblade(Dwarf dwarf) {
		super(dwarf);
	}
	
	private static final ItemStack ITEM = DwarvenItems.getItem("sword.drb");
	@Override public ItemStack getItem() {return ITEM;}
	@Override public KitGiveType getGiveType() { return KitGiveType.SWORD; }
}
