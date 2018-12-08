package deimophobe.nightfall.debug;

/**
 * Created by Deimophobe on 8/12/18.
 */
public interface Debuggable {
	void sendDebugMessage(String message, DebugCause debugCause);
}
