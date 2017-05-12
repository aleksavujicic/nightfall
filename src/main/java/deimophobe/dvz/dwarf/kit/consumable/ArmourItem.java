package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 23/01/17.
 */
class ArmourItem extends Consumable {
	ArmourItem(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isLeftClick(action)) {
			Dwarf toArmour = dwarf.getLookingAt(2, 7, DwarfManager.getManager().getDwarves(), (d) -> !d.getArmour().isArmoured());
			if (toArmour != null) {
				toArmour.getArmour().putOn();
				return DEFAULT_CD;
			}
		}
		return FAILED_CD;
	}
}
