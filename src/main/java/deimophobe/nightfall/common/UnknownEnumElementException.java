package deimophobe.nightfall.common;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class UnknownEnumElementException extends IllegalArgumentException {
	
	public UnknownEnumElementException(String message) {
		super(message);
	}
	
	public UnknownEnumElementException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
