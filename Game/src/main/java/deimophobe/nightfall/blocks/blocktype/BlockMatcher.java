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
		return new BlockSet(this, orBlock);
	}
	
	default BlockMatcher orOfMaterial(Material... materials) {
		checkNotNull(materials, "Materials must not be null.");
		return this.or(new MaterialSet(materials));
	}
	
	@Deprecated
	default BlockMatcher butAlso(BlockMatcher andBlock) {
		checkNotNull(andBlock, "And block must not be null.");
		return new BlockSet(this, andBlock);
	}
	
	@Deprecated
	default BlockMatcher butAlso(Material... materials) {
		checkNotNull(materials, "And block must not be null.");
		return this.butAlso(new MaterialSet(materials));
	}
	
	default BlockMatcher andAlso(BlockMatcher andMatcher) {
		checkNotNull(andMatcher, "And Matcher must not be null.");
		return block -> this.matchesBlock(block) && andMatcher.matchesBlock(block);
	}
	
	default BlockInteracter withPlacer(BlockPlacer placer) {
		checkNotNull(placer, "Placer must not be null.");
		return new BlockInteracter() {
			@Override
			public boolean matchesBlock(@NotNull Block block) {
				return BlockMatcher.this.matchesBlock(block);
			}
			
			@Override
			public void setAtBlock(@NotNull Block block) {
				placer.setAtBlock(block);
			}
		};
	}
}
