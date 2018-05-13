package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Step;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 20/06/17.
 */
public class SlabBlock implements CustomBlock {
	
	private final TexturedBlock stepBlock;
	private final TexturedBlock doubleSlabBlock;
	private final Boolean inverted;
	
	SlabBlock(Material texture) {
		stepBlock = new TexturedBlock(Material.STEP, texture);
		doubleSlabBlock = new TexturedBlock(Material.DOUBLE_STEP, texture);
		inverted = null;
	}
	
	SlabBlock(SlabBlock block, boolean inverted) {
		stepBlock = block.stepBlock;
		doubleSlabBlock = block.doubleSlabBlock;
		this.inverted = inverted;
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		if (block.getType() == Material.STEP) {
			Boolean inverted = this.inverted;
			if (inverted == null) inverted = ((Step) block.getState().getData()).isInverted();
			
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
	public boolean matchesBlock(@NotNull Block block) {
		return (stepBlock.matchesBlock(block) || doubleSlabBlock.matchesBlock(block));
	}
	
	public SlabBlock withInversion(boolean inversion) {
		return new SlabBlock(this, inversion);
	}
}
