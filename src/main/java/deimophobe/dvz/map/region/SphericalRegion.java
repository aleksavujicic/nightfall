package deimophobe.dvz.map.region;

import deimophobe.dvz.Misc;
import deimophobe.dvz.map.GameMap;
import deimophobe.dvz.map.InvalidMapConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 21/01/17.
 */
class SphericalRegion implements Region {
	
	private final Location center;
	private final double radius;
	
	SphericalRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		if (!section.contains("center"))
			throw new InvalidMapConfigException("Spherical region must specify center.", section);
		this.center = map.getLocation(section, "center");
		
		if (!section.contains("radius"))
			throw new InvalidMapConfigException("Spherical region must specify radius.", section);
		this.radius = section.getDouble("radius");
		
		if (radius == 0)
			throw new InvalidMapConfigException("Radius must be non-zero. (Use nullregion for empty regions)", section);
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		return (loc.distance(center) <= radius);
	}
}
