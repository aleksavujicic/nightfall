package deimophobe.nightfall.map;

/**
 * Thrown whenever a map world is unable to be loaded or unloaded (such as IO exceptions).
 * Created by Deimophobe on 2/07/17.
 */
public class MapLoadingException extends Exception {
	public MapLoadingException() {
	}
	
	public MapLoadingException(String s) {
		super(s);
	}
	
	public MapLoadingException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public MapLoadingException(Throwable throwable) {
		super(throwable);
	}
}
