package deimophobe.nightfall.map.region;

/**
 * Created by Deimophobe on 7/02/19.
 */
public class BadlyFormattedStringException extends Exception {
	
	public BadlyFormattedStringException() {}
	
	public BadlyFormattedStringException(String message) {
		super(message);
	}
	
	public BadlyFormattedStringException(Throwable cause) {
		super(cause);
	}
	
	public BadlyFormattedStringException(String message, Throwable cause) {
		super(message, cause);
	}
}
