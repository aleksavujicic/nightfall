package deimophobe.nightfall.cooldown;

/**
 * Created by Deimophobe on 31/03/18.
 */
public abstract class LifetimeExpireable implements Expirable {
	private int lifetime;
	
	protected LifetimeExpireable(int lifetime) {
		this.lifetime = lifetime;
	}
	
	@Override
	public boolean hasExpired() {
		return lifetime <= 0;
	}
	
	@Override
	public void update() {
		lifetime--;
	}
	
	public boolean everyNTicks(int n) {
		return lifetime % n == 0;
	}
}
