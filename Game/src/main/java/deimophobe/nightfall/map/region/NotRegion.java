package deimophobe.nightfall.map.region;

import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 12/05/18.
 */
class NotRegion implements Region {
	private final Region region;
	
	NotRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		ConfigurationSection regionSubsection = section.getConfigurationSection("region");
		if (regionSubsection == null) throw new InvalidMapConfigException("Not region needs a 'region' subregion");
		region = Region.createRegion(map, section.getConfigurationSection("region"));
	}
	
	@Override
	public boolean containsLocation(Location loc) {
		return !region.containsLocation(loc);
	}
}
