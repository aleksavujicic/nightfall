package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 1/07/17.
 */
public interface MapFeature {
	void activate(GameMap map, ConfigurationSection config) throws InvalidMapConfigException;
	void deactivate();
}
