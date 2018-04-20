package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 17/06/17.
 */
@FunctionalInterface
public interface ComparableBlock {
	boolean matchesBlock(Block block);
	
	default ComparableBlock except(ComparableBlock excepter) {
		return block -> !excepter.matchesBlock(block) && ComparableBlock.this.matchesBlock(block);
	}
	
	default ComparableBlock except(Material... materials) {
		return this.except(new MaterialSet(materials));
	}
}
