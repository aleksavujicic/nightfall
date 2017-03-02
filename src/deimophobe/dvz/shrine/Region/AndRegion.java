package deimophobe.dvz.shrine.Region;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 27/02/17.
 */
public class AndRegion implements Region {
	Set<Region> regions = new HashSet<>();
	
	AndRegion(ConfigurationSection section) {
		for (String key : section.getKeys(false)) {
			regions.add(Region.createRegion(section.getConfigurationSection(key)));
		}
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
