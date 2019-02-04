package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 20/05/17.
 */
public class DudCooldown implements Cooldown {
	@Override
	public void update() {}
	
	@Override
	public boolean tryUse() {
		return false;
	}
	
	@Override
	public boolean isAvailable() {
		return false;
	}
	
	@Override
	public void reset() {}
	
	@Override
	public float getCooldown() {
		return 0;
	}
	
	@Override
	public void forceAvailable() { }
	
	@Override
	public boolean wasUsedWithin(int duration) {
		return false;
	}
	
	@Override
	public int getTimeRemaining() {
		return 0;
	}
	
	@Override
	public int getTimeSinceUse() {
		return 0;
	}
}
