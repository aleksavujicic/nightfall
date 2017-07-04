package deimophobe.dvz.map.region;

import deimophobe.dvz.map.GameMap;
import deimophobe.dvz.map.InvalidMapConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 27/02/17.
 */
public class AndRegion implements Region {
	private final Set<Region> regions = new HashSet<>();
	
	AndRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		for (String key : section.getKeys(false)) {
			if (!key.equals("type") && !key.equals("center"))
				regions.add(Region.createRegion(map, section.getConfigurationSection(key)));
		}
		if (regions.size() < 2)
			throw new InvalidMapConfigException("And region must have at least 2 subregions", section);
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		for (Region region : regions) {
			if (!region.containsLocation(loc))
				return false;
		}
		return true;
	}
}
