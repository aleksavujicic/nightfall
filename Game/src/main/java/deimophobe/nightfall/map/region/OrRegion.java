package deimophobe.nightfall.map.region;

import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 27/02/17.
 */
class OrRegion implements Region {
	private final Set<Region> regions = new HashSet<>();
	
	OrRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		for (String key : section.getKeys(false)) {
			if (!key.equals("type") && !key.equals("center"))
				regions.add(Region.createRegion(map, section.getConfigurationSection(key)));
		}
		if (regions.size() < 2)
			throw new InvalidMapConfigException("Or region must have at least 2 subregions.", section);
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		for (Region region : regions) {
			if (region.containsLocation(loc))
				return true;
		}
		return false;
	}
}
