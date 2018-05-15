package deimophobe.nightfall.common;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 14/05/18.
 */
public class ConfigValidator {
	public static void checkChildExists(ConfigurationSection config, String child) throws MalformedConfigurationException {
		if (!config.contains(child)) throw new MalformedConfigurationException("Malformed configuration in '" + config.getName() + "'. Failed to find child '" + child + "'");
	}
}
