package deimophobe.dvz.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class DudCooldown implements Cooldown {
	@Override
	public void update() {}
	
	@Override
	public boolean isAvailable() {
		return false;
	}
	
	@Override
	public void reset() {}
	
	@Override
	public float fractionComplete() {
		return 0;
	}
}
