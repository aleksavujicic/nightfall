package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.blocks.blocktype.NFBlocks;
import deimophobe.nightfall.game.entity.GameEntity;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class DataTimedBlock extends TimedBlock {
	private final BlockData newData;
	
	private BlockData oldData;
	
	public DataTimedBlock(int lifeTime, Block block, GameEntity placer, BlockData newData) {
		super(lifeTime, block, placer);
		this.newData = newData;
	}
	
	@Override
	public boolean isPlaceable() {
		return !NFBlocks.UNTIMEABLE_BLOCKS.matchesBlock(block);
	}
	
	@Override
	protected void setBlock() {
		oldData = block.getBlockData();
		block.setBlockData(newData, false);
	}
	
	@Override
	protected void unsetBlock() {
		block.setBlockData(oldData);
	}
}
