package deimophobe.nightfall.cooldown;

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
		if (lifetime > 0) {
			lifetime--;
			
			if (lifetime == 0) onExpiry();
		} else {
			throw new IllegalStateException("Cannot update lifetime expireable if lifetime is 0");
		}
	}
	
	protected void onExpiry() {}
	
	public boolean everyNTicks(int n) {
		return lifetime % n == 0;
	}
	
	public void expire() {
		lifetime = 0;
	}
}
