package deimophobe.nightfall.cooldown;

import deimophobe.nightfall.NightfallPlugin;

/**
 * Created by Deimophobe on 31/03/18.
 */
public abstract class LifetimeExpireable implements Expirable {
	private final int maxLifetime;
	private int lifetime;
	
	protected LifetimeExpireable(int lifetime) {
		this.maxLifetime = lifetime;
		this.lifetime = lifetime;
	}
	
	@Override
	public boolean hasExpired() {
		return lifetime == 0;
	}
	
	@Override
	public void update() {
		if (!checkPositive()) return;
		lifetime--;
	}
	
	@Override
	public void onExpiry() {}
	
	public final int getLifetime() {
		return lifetime;
	}
	public final int getMaxLifetime() {
		return maxLifetime;
	}
	
	protected final boolean afterAtLeastNTicks(int n) { return lifetime <= maxLifetime - n; }
	protected final boolean afterExactlyNTicks(int n) { return lifetime == maxLifetime - n; }
	protected final boolean everyNTicks(int n) {
		return lifetime % n == 0;
	}
	
	protected final void expire() {
		lifetime = 0;
	}
	
	protected final void reduceLifetime(int amount) {
		if (!checkPositive()) return;
		
		lifetime = Math.max(lifetime - amount, 0);
	}
	
	private boolean checkPositive() {
		if (lifetime <= 0) {
			//throw new IllegalStateException("Cannot reduce lifetime expireable if lifetime is not positive. Object: " + this);
			// Perhaps it shouldn't break the game...
			NightfallPlugin.logger().severe("Cannot reduce lifetime expireable if lifetime is not positive. Object: " + this);
			return false;
		}
		return true;
	}
}
