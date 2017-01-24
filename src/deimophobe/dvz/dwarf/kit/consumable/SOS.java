package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class SOS extends Consumable {
	
	SOS(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		return false;
	}
}
