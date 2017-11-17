package deimophobe.nightfall;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/11/17.
 */
public abstract class CustomProjectile extends BukkitRunnable {
	protected Location location;
	protected Vector velocity;
	protected final World world;
	
	private int lifetime;
	private final int updateFreq;
	private final double gravity;
	
	protected CustomProjectile(int lifetime, Location location, Vector velocity, double gravity, int frequency) {
		this.lifetime = lifetime;
		this.updateFreq = frequency;
		this.location = location;
		this.world = location.getWorld();
		this.velocity = velocity;
		this.gravity = gravity;
		
		runTaskTimer(NightfallPlugin.getPlugin(), 0, frequency);
	}
	
	@Override
	public void run() {
		location.add(velocity);
		velocity.add(new Vector(0, -gravity, 0));
		
		lifetime -= updateFreq;
		if (lifetime <= 0)
			this.cancel();
		
		if (location.getBlock().getType().isSolid() || lifetime <= 0) {
			this.cancel();
		}
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
		lifetime = 0;
	}
	
	protected int getLifeLeft() {
		return lifetime;
	}
}
