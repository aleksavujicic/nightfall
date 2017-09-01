package deimophobe.nightfall.damage;

import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 1/09/17.
 */
public class DamageModifier {
	/** How much knockback to do. */
	private Vector knockback = null;
	/** If set to true, the damage will no longer occur. Overrides force. */
	private boolean cancelled = false;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	private boolean force = false;
	/** If set to true, damage will be 'infinite'. */
	private boolean instaKill = false;
	
	public void setKnockback(Vector knockback) {
		this.knockback = knockback;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public void setForce(boolean force) {
		this.force = force;
	}
	
	public void setInstaKill(boolean instaKill) {
		this.instaKill = instaKill;
	}
	
	public DamageModifier() {}
	
	void applyToDamage(GameDamage damage) {
		if (knockback != null) damage.setKnockback(knockback);
		if (cancelled) damage.cancel();
		if (force) damage.force();
		if (instaKill) damage.instaKill();
	}
}
