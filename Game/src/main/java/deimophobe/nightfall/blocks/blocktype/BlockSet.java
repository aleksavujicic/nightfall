package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class BlockSet implements BlockMatcher {
	
	private final Set<BlockMatcher> matcherSet;
	/** Used for quick look ups. BlockSet will try to 'disassemble' any matchers it gets that are just Material tests. */
	private final Set<Material> fastMaterialSet;
	
	public BlockSet(BlockMatcher... matchers) {
		matcherSet = new HashSet<>();
		fastMaterialSet = new HashSet<>();
		
		
		for (BlockMatcher matcher : matchers) {
			addBlockMatcher(matcher);
		}
	}
	
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		Material type = block.getType();
		if (fastMaterialSet.contains(type)) return true;
		
		for (BlockMatcher matcher : matcherSet) {
			if (matcher.matchesBlock(block)) {
				return true;
			}
		}
		
		return false;
	}
	
	// This will try to 'disassemble' any matcher and pick out and collect any materials together.
	// Done purely for performance reasons - some BlockMatchers may need to be run many times.
	private void addBlockMatcher(BlockMatcher matcher) {
		if (matcher instanceof MaterialBlock) {
			MaterialBlock materialBlock = (MaterialBlock) matcher;
			Material material = materialBlock.getMaterial();
			fastMaterialSet.add(material);
		}
		else if (matcher instanceof MaterialSet) {
			MaterialSet materialSet = (MaterialSet) matcher;
			Set<Material> materials = materialSet.getMaterialSet();
			fastMaterialSet.addAll(materials);
		}
		else if (matcher instanceof RepeatMaterial) {
			RepeatMaterial repeatMaterial = (RepeatMaterial) matcher;
			Set<Material> materials = repeatMaterial.getMaterials();
			fastMaterialSet.addAll(materials);
		}
		else if (matcher instanceof BlockSet) {
			BlockSet blockSet = (BlockSet) matcher;
			
			fastMaterialSet.addAll(blockSet.fastMaterialSet);
			matcherSet.addAll(blockSet.matcherSet);
		}
		else {
			// All else fails, just add to block set.
			matcherSet.add(matcher);
		}
	}
}
