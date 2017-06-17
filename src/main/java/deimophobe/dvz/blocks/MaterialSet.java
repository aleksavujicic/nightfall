package deimophobe.dvz.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class MaterialSet implements ComparableBlock {
	
	private final Set<Material> materialSet;
	
	public MaterialSet(Material... materials) {
		this.materialSet = new HashSet<>();
		
		for (Material material : materials)
			materialSet.add(material);
	}
	
	@Override
	public boolean matchesBlock(Block block) {
		return materialSet.contains(block.getType());
	}
}
