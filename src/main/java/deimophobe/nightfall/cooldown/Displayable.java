package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 31/01/18.
 */
public interface Displayable {
	Displayable DISPLAY_NOTHING = () -> 0;
	
	float getCooldown();
}
