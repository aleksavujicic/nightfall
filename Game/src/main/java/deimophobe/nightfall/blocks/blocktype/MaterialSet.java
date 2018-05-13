package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class MaterialSet implements CustomBlock {
	
	private final Set<Material> materialSet;
	private final Material first;
	
	public MaterialSet(Material... materials) {
		first = materials[0];
		
		this.materialSet = new HashSet<>();
		Collections.addAll(materialSet, materials);
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		return materialSet.contains(block.getType());
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		block.setType(first);
	}
}
