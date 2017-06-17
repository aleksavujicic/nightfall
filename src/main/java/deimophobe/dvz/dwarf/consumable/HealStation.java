package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.blocks.timedblock.HealBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealStation extends Consumable {
	public HealStation(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		boolean success =  TimedBlock.placeTimedBlock(new HealBlock(clickedBlock, 30*20, dwarf));
		
		if (success) return DEFAULT_CD;
		else return FAILED_CD;
	}
}
