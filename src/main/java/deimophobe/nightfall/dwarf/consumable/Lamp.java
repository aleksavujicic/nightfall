package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Lamp extends Consumable {
	
	Lamp(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		boolean success = TimedBlock.placeTimedBlock(new LampBlock(clickedBlock, 60*20, dwarf, true));
		
		if (success) return DEFAULT_CD;
		else return FAILED_CD;
	}
}
