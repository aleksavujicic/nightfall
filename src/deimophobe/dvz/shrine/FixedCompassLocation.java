package deimophobe.dvz.shrine;

import org.bukkit.Location;

/**
 * Created by Deimophobe on 31/03/17.
 */
public class FixedCompassLocation implements CompassLocation {
	private final Location location;
	private final String name;
	
	public FixedCompassLocation(String name, Location location) {
		this.location = location;
		this.name = name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public String getName() {
		return name;
	}
}
