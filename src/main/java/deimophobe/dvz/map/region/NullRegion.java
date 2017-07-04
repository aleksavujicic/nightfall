package deimophobe.dvz.map.region;

import org.bukkit.Location;

/**
 * Created by Deimophobe on 21/01/17.
 */
public class NullRegion implements Region {
	@Override
	public boolean containsLocation(Location loc) {
		return false;
	}
}
