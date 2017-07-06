package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/06/17.
 */
class MaterialSet implements ComparableBlock {
	
	private final Set<Material> materialSet;
	
	MaterialSet(Material... materials) {
		this.materialSet = new HashSet<>();
		
		Collections.addAll(materialSet, materials);
	}
	
	@Override
	public boolean matchesBlock(Block block) {
		return materialSet.contains(block.getType());
	}
}
