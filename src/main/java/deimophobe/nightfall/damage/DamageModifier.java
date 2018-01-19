package deimophobe.nightfall.damage;

import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 1/09/17.
 */
@Deprecated
public class DamageModifier {
	/** How much knockback to do. */
	private Vector knockback = null;
	private boolean addKnockback = false;
	/** If set to true, the damage will no longer occur. Overrides force. */
	private boolean cancelled = false;
	/** If set to true, damage will be 'infinite'. */
	private boolean instaKill = false;
	
	public DamageModifier setKnockback(Vector knockback) {
		this.knockback = knockback;
		addKnockback = false;
		return this;
	}
	
	public DamageModifier setKnockback(double x, double y, double z) {
		return setKnockback(new Vector(x,y,z));
	}
	
	public DamageModifier addKnockback(Vector knockback) {
		this.knockback = knockback;
		addKnockback = true;
		return this;
	}
	
	public DamageModifier addKnockback(double x, double y, double z) {
		return addKnockback(new Vector(x,y,z));
	}
	
	public DamageModifier cancel() {
		this.cancelled = true;
		return this;
	}
	
	public DamageModifier instaKill() {
		this.instaKill = true;
		return this;
	}
	
	public DamageModifier() {}
	
	public void applyToDamage(GameDamage damage) {
		if (knockback != null) {
			if (addKnockback)
				damage.addKnockback(knockback);
			else
				damage.setKnockback(knockback);
		}
		if (cancelled) damage.cancel();
		if (instaKill) damage.instaKill();
	}
}
