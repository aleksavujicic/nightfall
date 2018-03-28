package deimophobe.nightfall.damage.dot;

/**
 * Created by Deimophobe on 28/03/18.
 */
public class InvalidPoisonLevelException extends RuntimeException {
	
	public InvalidPoisonLevelException(String message) {
		super(message);
	}
	
	public InvalidPoisonLevelException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
