package deimophobe.nightfall.damage;

import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 1/09/17.
 */
public class DamageModifier {
	/** How much knockback to do. */
	private Vector knockback = null;
	private boolean addKnockback = false;
	/** If set to true, the damage will no longer occur. Overrides force. */
	private boolean cancelled = false;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	private boolean force = false;
	/** If set to true, damage will be 'infinite'. */
	private boolean instaKill = false;
	
	public DamageModifier setKnockback(Vector knockback) {
		this.knockback = knockback;
		addKnockback = false;
		return this;
	}
	
	public DamageModifier addKnockback(double x, double y, double z) {
		return addKnockback(new Vector(x,y,z));
	}
	
	public DamageModifier addKnockback(Vector knockback) {
		this.knockback = knockback;
		addKnockback = true;
		return this;
	}
	
	public DamageModifier setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
		return this;
	}
	
	public DamageModifier setForce(boolean force) {
		this.force = force;
		return this;
	}
	
	public DamageModifier setInstaKill(boolean instaKill) {
		this.instaKill = instaKill;
		return this;
	}
	
	public DamageModifier() {}
	
	void applyToDamage(GameDamage damage) {
		if (knockback != null) {
			if (addKnockback)
				damage.addKnockback(knockback);
			else
				damage.setKnockback(knockback);
		}
		if (cancelled) damage.cancel();
		if (force) damage.force();
		if (instaKill) damage.instaKill();
	}
}
