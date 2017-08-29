package deimophobe.nightfall.damage;

import deimophobe.nightfall.Misc;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 23/08/17.
 */
public class ArrowDamageData {
	private final Projectile arrow;
	public Projectile getArrow() { return arrow; }
	public float getForce() { return Misc.getArrowForce(arrow); }
	
	public ArrowDamageData(Projectile arrow) {
		this.arrow = arrow;
	}
}
