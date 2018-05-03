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
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		if (face == null) return FAILED_CD;
		
		Block block = clickedBlock.getRelative(face);
		boolean success = BlockManager.getManager().placeTimedBlock(new JumpPad(block, dwarf));
		
		if (success) return DEFAULT_CD;
		else return FAILED_CD;
		
	}
}
