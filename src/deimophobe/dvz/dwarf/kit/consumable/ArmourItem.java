package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
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
		Dwarf toArmour = dwarf.getLookingAt(2, 7, (d) -> !d.isArmoured(), DwarfManager.getManager());
		if (toArmour != null) {
			toArmour.putOnArmour();
			return true;
		}
		return false;
	}
}
