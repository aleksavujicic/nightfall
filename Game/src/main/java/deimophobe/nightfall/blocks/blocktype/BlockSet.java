package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 17/06/17.
 */
public class BlockSet implements ComparableBlock {
	
	private final Set<ComparableBlock> blockSet;
	
	public BlockSet(ComparableBlock... blocks) {
		this.blockSet = new HashSet<>();
		
		
		Collections.addAll(blockSet, blocks);
	}
	
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		for (ComparableBlock cBlock : blockSet)
			if (cBlock.matchesBlock(block))
				return true;
		
		return false;
	}
}
