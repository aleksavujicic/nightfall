package deimophobe.nightfall.cooldown;

import deimophobe.nightfall.NightfallPlugin;

/**
 * Created by Deimophobe on 31/03/18.
 */
public abstract class LifetimeExpireable implements Expirable {
	private int lifetime;
	
	public int getLifetime() {
		return lifetime;
	}
	
	protected LifetimeExpireable(int lifetime) {
		this.lifetime = lifetime;
	}
	
	@Override
	public boolean hasExpired() {
		return lifetime == 0;
	}
	
	@Override
	public void update() {
		checkPositive();
		lifetime--;
	}
	
	@Override
	public void onExpiry() {}
	
	public boolean everyNTicks(int n) {
		return lifetime % n == 0;
	}
	
	public void expire() {
		lifetime = 0;
	}
	
	public void reduceLifetime(int amount) {
		checkPositive();
		
		// Lets it tick once more
		lifetime = Math.max(lifetime - amount, 1);
	}
	
	private void checkPositive() {
		if (lifetime <= 0) {
			//throw new IllegalStateException("Cannot reduce lifetime expireable if lifetime is not positive. Object: " + this);
			// Perhaps it shouldn't break the game...
			NightfallPlugin.logger().severe("Cannot reduce lifetime expireable if lifetime is not positive. Object: " + this);
		}
	}
}
