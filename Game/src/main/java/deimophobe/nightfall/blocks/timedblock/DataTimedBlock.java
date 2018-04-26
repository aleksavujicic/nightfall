package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.game.GameEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 25/04/18.
 */
public class DataTimedBlock extends TimedBlock {
	private final Material newType;
	
	private Material oldType;
	private byte data;
	
	public DataTimedBlock(int lifeTime, Block block, GameEntity placer, Material blockType) {
		super(lifeTime, block, placer);
		this.newType = blockType;
	}
	
	@Override
	public boolean isPlaceable() {
		return !BlockType.UNTIMEABLE_BLOCKS.matchesBlock(block);
	}
	
	@Override
	protected void setBlock() {
		oldType = block.getType();
		data = block.getData();
		
		block.setType(newType);
	}
	
	@Override
	protected void unsetBlock() {
		block.setType(oldType);
		block.setData(data);
	}
}
