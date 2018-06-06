package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.JumpPad;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 3/05/18.
 */
public class JumpConsumable extends Consumable {
	protected JumpConsumable(String itemName) { super(itemName); }
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		if (face == null) return ConsumeResult.FAILURE;
		
		Block block = clickedBlock.getRelative(face);
		boolean success = BlockManager.getManager().placeTimedBlock(new JumpPad(block, dwarf));
		
		if (success) return ConsumeResult.SUCCESS;
		else return ConsumeResult.FAILURE;
	}
}
