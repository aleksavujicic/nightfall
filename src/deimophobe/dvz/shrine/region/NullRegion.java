package deimophobe.dvz.shrine.region;

import org.bukkit.Location;

/**
 * Created by Deimophobe on 21/01/17.
 */
class NullRegion implements Region {
	@Override
	public boolean containsLocation(Location loc) {
		return false;
	}
}
