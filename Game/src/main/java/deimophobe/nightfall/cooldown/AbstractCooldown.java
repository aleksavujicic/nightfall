package deimophobe.nightfall.cooldown;

/**
 * Cooldowns are hard and do lots of crazy things. I wanna actually
 * get something that isn't just a copy paste mess. Hopefully this works out.
 *
 * Created by Deimophobe on 1/05/18.
 */
public abstract class AbstractCooldown implements Cooldown {
	private final int maxTime;
	private int currentTime;
	
	protected AbstractCooldown(int maxTime) {
		if (maxTime <= 0) throw new IllegalArgumentException("Cooldown max time must be strictly positive.");
		
		this.maxTime = maxTime;
		this.currentTime = 0;
	}
	
	@Override
	public void update() {
		reduceCooldown(1);
	}
	
	@Override
	public float getCooldown() {
		return 1 - (float) currentTime/maxTime;
	}
	
	@Override
	public void reset() {
		currentTime = maxTime;
	}
	
	@Override
	public boolean isAvailable() {
		return currentTime == 0;
	}
	
	// TODO some of these should be made a part of the Cooldown interface probably.
	
	public boolean tryUse() {
		if (!isAvailable()) return false;
		if (!canUse()) return false;
		
		reset();
		onUse();
		return true;
	}
	
	public void reduceCooldown(int amount) {
		if (currentTime == 0) return;
		
		currentTime -= amount;
		if (currentTime <= 0) {
			currentTime = 0;
			onCooldownCompletion();
		}
	}
	
	public boolean wasUsedWithin(int time) {
		return (currentTime >= maxTime - time);
	}
	
	protected abstract void onCooldownCompletion();
	protected abstract boolean canUse();
	protected abstract void onUse();
}
