package deimophobe.dvz.shrine.region;

import deimophobe.dvz.Misc;
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
	
	CylindricalRegion(ConfigurationSection section) {
		List<Double> doubles = section.getDoubleList("center");
		x = doubles.get(0);
		z = doubles.get(1);
		
		this.radius = section.getDouble("radius");
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		double diffx = loc.getX() - x;
		double diffz = loc.getZ() - z;
		return (diffx*diffx + diffz*diffz <= radius*radius);
	}
}
