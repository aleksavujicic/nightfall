package deimophobe.nightfall.map.feature;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 1/07/17.
 */
public interface MapFeature {
	void activate(ConfigurationSection config);
	void deactivate();
}
