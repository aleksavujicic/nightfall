package deimophobe.nightfall.util;

import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class Util {
	
	public static boolean isWater(Block block) {
		switch (block.getType()) {
			case WATER:
			case STATIONARY_WATER:
				return true;
				
			default:
				return false;
		}
	}
}
