package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 17/06/17.
 */
@FunctionalInterface
public interface BlockMatcher {
	boolean matchesBlock(@NotNull Block block);
	
	default BlockMatcher except(BlockMatcher exception) {
		checkNotNull(exception, "Exception must not be null.");
		return block -> this.matchesBlock(block) && !exception.matchesBlock(block);
	}
	
	default BlockMatcher except(Material... materials) {
		checkNotNull(materials, "Materials must not be null.");
		return this.except(new MaterialSet(materials));
	}
	
	default BlockMatcher or(BlockMatcher orBlock) {
		checkNotNull(orBlock, "Or block must not be null.");
		return block -> this.matchesBlock(block) || orBlock.matchesBlock(block);
	}
	
	default BlockMatcher orOfMaterial(Material... materials) {
		checkNotNull(materials, "Materials must not be null.");
		return this.or(new MaterialSet(materials));
	}
	
	default BlockMatcher butAlso(BlockMatcher andBlock) {
		checkNotNull(andBlock, "And block must not be null.");
		return block -> this.matchesBlock(block) || andBlock.matchesBlock(block);
	}
	
	default BlockMatcher butAlso(Material... materials) {
		checkNotNull(materials, "And block must not be null.");
		return this.butAlso(new MaterialSet(materials));
	}
}
