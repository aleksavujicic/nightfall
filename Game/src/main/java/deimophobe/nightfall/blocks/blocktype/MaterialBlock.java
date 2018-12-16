package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class MaterialBlock implements BlockInteracter {
	private final Material material;
	public MaterialBlock(Material material) {
		this.material = material;
	}
	
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		block.setType(material);
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		return (block.getType() == material);
	}
	
	public Material getMaterial() {
		return material;
	}
}
