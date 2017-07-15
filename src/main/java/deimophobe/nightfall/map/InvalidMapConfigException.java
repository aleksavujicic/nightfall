package deimophobe.nightfall.map;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Thrown when the config file for a map is invalid.
 * Created by Deimophobe on 2/07/17.
 */
public class InvalidMapConfigException extends Exception {
	public InvalidMapConfigException() {
	}
	
	public InvalidMapConfigException(String s) {
		super(s);
	}
	
	public InvalidMapConfigException(String s, ConfigurationSection config) {
		this(s + "\nPath: " + config.getCurrentPath());
	}
	
	public InvalidMapConfigException(String s, ConfigurationSection config, String key) {
		this(s + "\nPath: " + config.getCurrentPath() + "." + key);
	}
	
	public InvalidMapConfigException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public InvalidMapConfigException(Throwable throwable) {
		super(throwable);
	}
}
