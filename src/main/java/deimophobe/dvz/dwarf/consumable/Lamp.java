package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
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
		boolean success = TimedBlock.placeTimedBlock(new LampBlock(clickedBlock, 60*20, dwarf));
		
		if (success) return DEFAULT_CD;
		else return FAILED_CD;
	}
}
