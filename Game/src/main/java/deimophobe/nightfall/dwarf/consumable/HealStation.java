package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.HealBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealStation extends Consumable {
	private static final ConsumeResult TOO_CLOSE = ConsumeResult.failedResultWithMessage("Too close to another heal station");
	
	public HealStation(String item) {
		super(item);
	}
	
	@Override
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		
		// Check no other nearby heal blocks
		Location blockLoc = clickedBlock.getLocation();
		for (HealBlock healBlock : BlockManager.getManager().getTimedBlocks(HealBlock.class)) {
			Location healLoc = healBlock.getBlock().getLocation();
			if (blockLoc.distance(healLoc) < 12) {
				return TOO_CLOSE;
			}
		}
		
		// Place it
		HealBlock healBlock = new HealBlock(clickedBlock, 20*20, dwarf);
		boolean success =  BlockManager.getManager().placeTimedBlock(healBlock);
		
		return (success ? ConsumeResult.SUCCESS : ConsumeResult.FAILURE);
	}
}
