package deimophobe.dvz.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Step;

/**
 * Created by Deimophobe on 20/06/17.
 */
class SlabBlock implements CustomBlock {
	
	private final TexturedBlock stepBlock;
	private final TexturedBlock doubleSlabBlock;
	
	SlabBlock(Material texture) {
		stepBlock = new TexturedBlock(Material.STEP, texture);
		doubleSlabBlock = new TexturedBlock(Material.DOUBLE_STEP, texture);
	}
	
	@Override
	public void setAtBlock(Block block) {
		if (block.getType() == Material.STEP) {
			boolean inverted = ((Step) block.getState().getData()).isInverted();
			
			stepBlock.setAtBlock(block);
			
			BlockState state = block.getState();
			((Step) state.getData()).setInverted(inverted);
			state.update();
		} else if (block.getType() == Material.DOUBLE_STEP) {
			doubleSlabBlock.setAtBlock(block);
		} else {
			throw new IllegalArgumentException("Cannot set non-slab block to slab.");
		}
	}
	
	@Override
	public boolean matchesBlock(Block block) {
		return (stepBlock.matchesBlock(block) || doubleSlabBlock.matchesBlock(block));
	}
}
