package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 15/12/18.
 */
public class DatableBlock extends MaterialBlock {
	public DatableBlock(Material material) {
		super(material);
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		BlockData oldData = block.getBlockData();
		super.setAtBlock(block);
		
		block.setBlockData(oldData, false);
	}
}
