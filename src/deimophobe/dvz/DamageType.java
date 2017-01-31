package deimophobe.dvz;

import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 20/01/17.
 */
public enum DamageType {
	REGULAR_MELEE(true, false, false, false, false),
	REGULAR_RANGED(false, true, false, false, false),
	
	POISON(false, false, false, true, false),
	
	EBOW(false, true, false, false, true),
	HAMMER_AOE(true, false, false, false, true),
	EVISCERATE(true, false, false, false, true),
	
	EXPLOSION(false, false, false, false, true),
	
	NAT_CONTACT(false, false, true, false, false),
	NAT_DROWNING(false, false, true, false, false),
	NAT_FALL(false, false, true, false, false),
	NAT_HOT_FLOOR(false, false, true, false, false),
	NAT_CRAMMING(false, false, true, false, false),
	NAT_FALLING_BLOCK(false, false, true, false, false),
	NAT_LIGHTNING(false, false, true, false, false),
	NAT_LAVA(false, false, true, false, false),
	NAT_FIRE(false, false, true, false, false),
	
	NAT_VOID(false, false, false, false, false),
	
	INSTA_KILL(false, false, false, false, true)
	;
	
	private final boolean melee;
	private final boolean ranged;
	private final boolean natural;
	private final boolean poison;
	private final boolean custom;
	
	public boolean isMelee() {
		return melee;
	}
	
	public boolean isRanged() {
		return ranged;
	}
	
	public boolean isNatural() {
		return natural;
	}
	
	public boolean isPoison() {
		return poison;
	}
	
	public boolean isCustom() {
		return custom;
	}
	
	DamageType(boolean melee, boolean ranged, boolean natural, boolean poison, boolean custom) {
		this.melee = melee;
		this.ranged = ranged;
		this.natural = natural;
		this.poison = poison;
		this.custom = custom;
	}
}
