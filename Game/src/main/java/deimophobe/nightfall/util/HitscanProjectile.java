package deimophobe.nightfall.util;

import deimophobe.nightfall.game.GamePlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class HitscanProjectile extends CustomProjectile {
	
	private final Hitscan hitscan;
	
	public static HitscanProjectile fireProjectile(GamePlayer player, double velocity, double range, Hitscan hitscan) {
		int lifetime = (int) (range/velocity);
		
		Hitscan.FireLocation fireLocation = new Hitscan.FireLocation(player, range);
		Vector vectorVel = fireLocation.getDirection().multiply(velocity);
		
		return new HitscanProjectile(lifetime, fireLocation.getLocation(), vectorVel, hitscan);
	}
	
	public static HitscanProjectile fireProjectile(Hitscan.FireLocation fireLocation, double velocity, Hitscan hitscan) {
		int lifetime = (int) (fireLocation.getRange()/velocity);
		Vector vectorVel = fireLocation.getDirection().multiply(velocity);
		
		return new HitscanProjectile(lifetime, fireLocation.getLocation(), vectorVel, hitscan);
	}
	
	public HitscanProjectile(int lifetime, Location location, Vector velocity, Hitscan hitscan) {
		super(lifetime, location, velocity, 0);
		this.hitscan = hitscan;
	}
	
	@Override
	public void update() {
		boolean success = hitscan.fire(location, velocity.length());
		super.update();
		
		if (!success) {
			this.expire();
		}
	}
}
