package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 17/06/17.
 */
@FunctionalInterface
public interface ComparableBlock {
	boolean matchesBlock(@NotNull Block block);
	
	default ComparableBlock except(ComparableBlock exception) {
		checkNotNull(exception, "Exception must not be null.");
		return block -> this.matchesBlock(block) && !exception.matchesBlock(block);
	}
	
	default ComparableBlock except(Material... materials) {
		checkNotNull(materials, "Materials must not be null.");
		return this.except(new MaterialSet(materials));
	}
	
	default ComparableBlock or(ComparableBlock orBlock) {
		checkNotNull(orBlock, "Or block must not be null.");
		return block -> this.matchesBlock(block) || orBlock.matchesBlock(block);
	}
	
	default ComparableBlock orOfMaterial(Material... materials) {
		checkNotNull(materials, "Materials must not be null.");
		return this.or(new MaterialSet(materials));
	}
	
	default ComparableBlock butAlso(ComparableBlock andBlock) {
		checkNotNull(andBlock, "And block must not be null.");
		return block -> this.matchesBlock(block) || andBlock.matchesBlock(block);
	}
}
