package deimophobe.nightfall.damage;

import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 23/08/17.
 */
@Deprecated
public class ArrowDamageData {
	
	
	private final Projectile projectile;
	public Projectile getProjectile() { return projectile; }
	
	private final float force;
	public float getForce() {return force;}
	
	public void setBaseDamage(double dmg) {
		
	}
	
	public ArrowDamageData(Projectile projectile) {
		this.projectile = projectile;
		this.force = getForceOfProjectile(projectile);
	}
	
	public static float getForceOfProjectile(Projectile projectile) {
		if (!projectile.hasMetadata("force"))
			throw new IllegalArgumentException("Arrow is not player arrow so has no force.");
		
		return projectile.getMetadata("force").get(0).asFloat();
	}
	
	public static void setProjectileDamage(Projectile projectile, double damage) {
	}
	
	public static void setProjectileDamage(Projectile projectile, double damage, float force) {
		
	}
}
