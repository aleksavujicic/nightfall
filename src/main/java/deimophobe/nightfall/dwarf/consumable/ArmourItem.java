package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

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
