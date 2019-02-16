package deimophobe.nightfall.map.config;

/**
 * Created by Deimophobe on 12/02/19.
 */
public class InvalidMapConfigurationException extends Exception {
	
	public InvalidMapConfigurationException() {}
	
	public InvalidMapConfigurationException(String message) {
		super(message);
	}
	
	public InvalidMapConfigurationException(Throwable cause) {
		super(cause);
	}
	
	public InvalidMapConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
