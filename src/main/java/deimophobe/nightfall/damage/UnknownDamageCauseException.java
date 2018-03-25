package deimophobe.nightfall.damage;

/**
 * Created by Deimophobe on 25/03/18.
 */
public class UnknownDamageCauseException extends Exception {
	
	public UnknownDamageCauseException(String message) {
		super(message);
	}
	
	public UnknownDamageCauseException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
