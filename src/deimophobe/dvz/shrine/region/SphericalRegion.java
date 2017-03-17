package deimophobe.dvz.shrine.region;

import deimophobe.dvz.Misc;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 21/01/17.
 */
class SphericalRegion implements Region {
	
	private final Location center;
	private final double radius;
	
	SphericalRegion(Location center, double radius) {
		this.center = center;
		this.radius = radius;
	}
	
	SphericalRegion(ConfigurationSection section) {
		this.center = Misc.createLocation(section.getDoubleList("center"));
		this.radius = section.getDouble("radius");
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		return (loc.distance(center) <= radius);
	}
}
