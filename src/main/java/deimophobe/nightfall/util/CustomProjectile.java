package deimophobe.nightfall.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/11/17.
 */
public abstract class CustomProjectile extends LifetimeObject {
	protected Location location;
	protected Vector velocity;
	protected final World world;
	
	private final double gravity;
	
	protected CustomProjectile(int lifetime, Location location, Vector velocity, double gravity, int frequency) {
		super(lifetime, frequency);
		this.location = location;
		this.world = location.getWorld();
		this.velocity = velocity;
		this.gravity = gravity;
		
	}
	
	@Override
	public void run() {
		super.run();
		location.add(velocity);
		velocity.add(new Vector(0, -gravity, 0));
		
		if (location.getBlock().getType().isSolid()) {
			this.cancel();
		}
	}
}
