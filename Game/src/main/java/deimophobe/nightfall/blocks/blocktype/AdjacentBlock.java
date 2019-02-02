package deimophobe.nightfall.blocks.blocktype;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 10/01/19.
 */
public class AdjacentBlock implements BlockMatcher {
	private final BlockMatcher matcher;
	private final Set<Offset> offsets;
	
	private static final Set<Offset> CUBE;
	static {
		CUBE = new HashSet<>();
		for (int x=-1; x<=1; x++) {
			for (int y=-1; y<=1; y++) {
				for (int z=-1; z<=1; z++) {
					CUBE.add(new Offset(x,y,z));
				}
			}
		}
	}
	
	public static AdjacentBlock cubeAround(BlockMatcher matcher) {
		return new AdjacentBlock(matcher, CUBE);
	}
	
	private AdjacentBlock(BlockMatcher matcher, Set<Offset> offsets) {
		this.matcher = matcher;
		this.offsets = offsets;
	}
	
	
	
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		for (Offset offset : offsets) {
			Block testBlock = offset.getOffset(block);
			if (matcher.matchesBlock(testBlock)) return true;
		}
		return false;
	}
	
	private static class Offset {
		private final int x;
		private final int y;
		private final int z;
		
		private Offset(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		private Block getOffset(Block block) {
			return block.getRelative(x,y,z);
		}
	}
}
