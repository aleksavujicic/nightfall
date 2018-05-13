package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 10/03/17.
 */
@Deprecated
class DataBlock implements CustomBlock {
	private final Material material;
	private final byte data;
	
	DataBlock(Material material, byte data) {
		this.material = material;
		this.data = data;
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		block.setType(material);
		block.setData(data);
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		boolean dataMatches = (data == block.getData());
		return (block.getType() == material && dataMatches);
	}
}
