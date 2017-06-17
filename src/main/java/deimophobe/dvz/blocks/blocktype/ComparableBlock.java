package deimophobe.dvz.blocks.blocktype;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 17/06/17.
 */
public interface ComparableBlock {
	boolean matchesBlock(Block block);
}
