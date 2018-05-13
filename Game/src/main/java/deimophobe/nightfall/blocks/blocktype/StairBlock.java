package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 20/06/17.
 */
class StairBlock extends MaterialBlock {
	StairBlock(Material material) {
		super(material);
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		MaterialData data = block.getState().getData();
		
		super.setAtBlock(block);
		
		if (data instanceof Stairs) {
			BlockState state = block.getState();
			state.setData(data);
			state.update();
		} else {
			throw new IllegalArgumentException("Cannot set non-stair block to stairs.");
		}
	}
}
