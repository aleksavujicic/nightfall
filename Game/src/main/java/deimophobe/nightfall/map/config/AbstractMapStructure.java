package deimophobe.nightfall.map.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 12/02/19.
 */
abstract class AbstractMapStructure {
	private final String name;
	protected final ConfigurationSection configuration;
	
	protected AbstractMapStructure(String name, ConfigurationSection configuration) throws InvalidMapConfigurationException {
		checkNotNull(name, "Name must not be null");
		checkNotNull(configuration, "Configuration must not be null");
		
		this.name = name;
		this.configuration = configuration;
	}
	
	protected final void checkContains(String sectionName) throws InvalidMapConfigurationException {
		check(configuration.contains(sectionName), "Structure '%s' is missing configuration '%s'", name, sectionName);
	}
	
	protected final void checkNotNull(Object object, String message, Object... objects) throws InvalidMapConfigurationException {
		check(object != null, message, objects);
	}
	
	protected final void check(boolean expression, String message, Object... objects) throws InvalidMapConfigurationException {
		if (expression) return;
		
		throw new InvalidMapConfigurationException(
				String.format(message, objects)
		);
	}
}
