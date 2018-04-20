package deimophobe.nightfall.lobby.game.map;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class InvalidMapConfigException extends Exception {
	
	public InvalidMapConfigException(String message) {
		super(message);
	}
	
	public InvalidMapConfigException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
