package deimophobe.nightfall.util;

import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.game.Game;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 10/11/17.
 */
public abstract class CustomProjectile extends LifetimeExpireable {
	protected Location location;
	protected Vector velocity;
	protected final World world;
	
	private final double gravity;
	
	protected CustomProjectile(int lifetime, Location location, Vector velocity, double gravity) {
		super(lifetime);
		this.location = location;
		this.world = location.getWorld();
		this.velocity = velocity;
		this.gravity = gravity;
		
		Game.getGame().addUpdateable(this);
	}
	
	@Override
	public void update() {
		super.update();
		location.add(velocity);
		velocity.add(new Vector(0, -gravity, 0));
		location.setDirection(velocity);
		
		if (location.getBlock().getType().isSolid()) {
			this.expire();
		}
	}
}
