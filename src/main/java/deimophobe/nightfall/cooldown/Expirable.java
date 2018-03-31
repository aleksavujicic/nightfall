package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 27/01/18.
 */
public interface Expirable extends Updateable {
	boolean hasExpired();
}
