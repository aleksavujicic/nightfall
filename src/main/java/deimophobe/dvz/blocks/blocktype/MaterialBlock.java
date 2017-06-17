package deimophobe.dvz.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 17/06/17.
 */
class MaterialBlock implements CustomBlock {
	private final Material material;
	MaterialBlock(Material material) {
		this.material = material;
	}
	
	
	@Override
	public void setAtBlock(Block block) {
		block.setType(material);
	}
	
	@Override
	public boolean matchesBlock(Block block) {
		return (block.getType() == material);
	}
}
