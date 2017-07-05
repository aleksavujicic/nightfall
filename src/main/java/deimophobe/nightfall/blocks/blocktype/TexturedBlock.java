package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TexturedMaterial;

/**
 * Created by Deimophobe on 20/06/17.
 */
class TexturedBlock implements CustomBlock {
	
	private final Material material;
	private final Material texture;
	
	TexturedBlock(Material material, Material texture) {
		this.material = material;
		this.texture = texture;
	}
	
	
	@Override
	public void setAtBlock(Block block) {
		block.setType(material);
		BlockState state = block.getState();
		MaterialData data = state.getData();
		if (data instanceof TexturedMaterial) {
			((TexturedMaterial) data).setMaterial(texture);
			state.setData(data);
			state.update();
		} else {
			throw new IllegalArgumentException("Material " + material + " is not a textured block!");
		}
	}
	
	@Override
	public boolean matchesBlock(Block block) {
		if (block.getType() == material) {
			MaterialData data = block.getState().getData();
			if (data instanceof TexturedMaterial) {
				return (((TexturedMaterial) data).getMaterial() == texture);
			}
		}
		return false;
	}
}
