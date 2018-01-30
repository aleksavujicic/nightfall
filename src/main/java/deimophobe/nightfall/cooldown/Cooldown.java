package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
public interface Cooldown extends Updateable {
	
	void update();
	boolean isAvailable();
	void reset();
	
	float fractionComplete();
	
	
	default boolean resetIfAvailable() {
		boolean avail = isAvailable();
		
		if (avail)
			reset();
		
		return avail;
	}
}
