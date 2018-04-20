package deimophobe.nightfall.monster.doom;

/**
 * Created by Deimophobe on 29/03/18.
 */
public class DoomMetaMissingException extends RuntimeException {
	
	public DoomMetaMissingException(String message) {
		super(message);
	}
	
	public DoomMetaMissingException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
