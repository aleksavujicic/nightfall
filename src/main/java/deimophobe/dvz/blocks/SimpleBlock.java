package deimophobe.dvz.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class SimpleBlock implements CustomBlock {
	private final Material material;
	public SimpleBlock(Material material) {
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
