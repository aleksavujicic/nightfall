package deimophobe.nightfall.damage;

import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 23/08/17.
 */
@Deprecated
public class ArrowDamageData {
	
	
	private final Projectile projectile;
	public Projectile getProjectile() { return projectile; }
	
	
	public void setBaseDamage(double dmg) {
		
	}
	
	public ArrowDamageData(Projectile projectile) {
		this.projectile = projectile;
	}
	
	public static void setProjectileDamage(Projectile projectile, double damage) {
	}
	
	public static void setProjectileDamage(Projectile projectile, double damage, float force) {
		
	}
}
