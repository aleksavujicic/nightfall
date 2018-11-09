package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
// TODO probably needs more stuff.
public interface Cooldown extends Updateable, Displayable {
	boolean isAvailable();
	void reset();
	boolean tryUse();
	void forceAvailable();
}
