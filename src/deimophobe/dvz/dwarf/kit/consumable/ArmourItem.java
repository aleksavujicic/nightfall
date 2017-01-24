package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 23/01/17.
 */
class ArmourItem extends Consumable {
	ArmourItem(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		Dwarf toArmour = dwarf.getLookingAt(2, 7);
		if (toArmour != null && !toArmour.isArmoured()) {
			toArmour.putOnArmour();
			return true;
		}
		return false;
	}
}
