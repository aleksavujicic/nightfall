package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 17/06/17.
 */
class WoolBlock implements CustomBlock {
	
	private final Wool wool;
	WoolBlock(DyeColor color) {
		wool = new Wool(color);
	}
	
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		block.setType(Material.WOOL);
		
		BlockState state = block.getState();
		state.setData(wool);
		state.update();
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		MaterialData data = block.getState().getData();
		return data.equals(wool);
		//if (data.getItemType() == Material.WOOL)
		//	Wool
	}
}
