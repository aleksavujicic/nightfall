package deimophobe.nightfall.map.region;

import org.bukkit.World;

/**
 * Created by Deimophobe on 12/02/19.
 */
interface ParsedRegion {
	Region toRegion(World world);
}
