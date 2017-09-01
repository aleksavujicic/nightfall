package deimophobe.nightfall.damage;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 23/08/17.
 */
public class ArrowDamageData {
	private final Projectile arrow;
	public Projectile getArrow() { return arrow; }
	
	public float getForce() {
		if (!(arrow instanceof Arrow))
			throw new IllegalArgumentException("Arrow not actually an arrow.");
		
		if (!arrow.hasMetadata("force"))
			throw new IllegalArgumentException("Arrow is not player arrow so has no force.");
		
		return arrow.getMetadata("force").get(0).asFloat();
	}
	
	public ArrowDamageData(Projectile arrow) {
		this.arrow = arrow;
	}
}
