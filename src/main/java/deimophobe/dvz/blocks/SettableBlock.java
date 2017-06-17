package deimophobe.dvz.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 17/06/17.
 */
public interface SettableBlock {
	void setAtBlock(Block block);
	default void setAtLocation(Location location) {
		setAtBlock(location.getBlock());
	}
}
