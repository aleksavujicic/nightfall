package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Wrench extends Consumable {
	
	Wrench(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		
		boolean success = ShrineManager.getManager().useGold(15);
		if (success) {
			dwarf.repairArmour(10000);
			dwarf.playSound("block.anvil.use", 20, 0.8f, false);
			return DEFAULT_CD;
		} else {
			return FAILED_CD;
		}
	}
}
