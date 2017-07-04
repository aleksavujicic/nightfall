package deimophobe.dvz.map.region;

import deimophobe.dvz.map.GameMap;
import deimophobe.dvz.map.InvalidMapConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * Created by Deimophobe on 18/03/17.
 */
class CylindricalRegion implements Region {
	private final double x;
	private final double z;
	private final double radius;
	
	CylindricalRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		if (!section.contains("center"))
			throw new InvalidMapConfigException("Cylindrical region must specify center.", section);
		
		List<Double> doubles = section.getDoubleList("center");
		if (doubles.size() != 2)
			throw new InvalidMapConfigException("Cylindrical center must have exactly 2 coordinates (x,z).", section);
		
		x = doubles.get(0);
		z = doubles.get(1);
		
		if (!section.contains("radius"))
			throw new InvalidMapConfigException("Cylindrical region must specify radius.", section);
		this.radius = section.getDouble("radius");
		
		if (radius == 0)
			throw new InvalidMapConfigException("Radius must be non-zero. (Use nullregion for empty regions)", section);
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		double diffx = loc.getX() - x;
		double diffz = loc.getZ() - z;
		return (diffx*diffx + diffz*diffz <= radius*radius);
	}
}
