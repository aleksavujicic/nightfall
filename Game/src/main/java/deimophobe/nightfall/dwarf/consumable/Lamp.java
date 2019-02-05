package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Lamp extends Consumable {
	
	Lamp(String item) {
		super(item);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, @Nullable Block clickedBlock, BlockFace face) {
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		if (clickedBlock == null) return ConsumeResult.FAILURE;
		
		boolean success = BlockManager.getManager().placeTimedBlock(new LampBlock(clickedBlock, 60*20, dwarf, true));
		
		if (success) return ConsumeResult.SUCCESS;
		else return ConsumeResult.FAILURE;
	}
}
