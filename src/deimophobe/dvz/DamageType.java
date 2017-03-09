package deimophobe.dvz;

/**
 * Created by Deimophobe on 20/01/17.
 */
public enum DamageType {
	REGULAR_MELEE(true, false, false, false, false, false),
	REGULAR_RANGED(false, true, false, false, false, false),
	
	POISON(false, false, false, true, false, false),
	
	EXPLOSION(false, false, false, false, true, false),
	
	CONTACT(false, false, true, false, false, false),
	DROWNING(false, false, true, false, false, false),
	FALL(false, false, true, false, false, false),
	HOT_FLOOR(false, false, true, false, false, false),
	CRAMMING(false, false, true, false, false, false),
	FALLING_BLOCK(false, false, true, false, false, false),
	LIGHTNING(false, false, true, false, false, false),
	LAVA(false, false, true, false, false, false),
	FIRE(false, false, true, false, false, false),
	
	VOID(false, false, false, false, false, true),
	SEPPUKU(false, false, false, false, true, true),
	SHRINE_PROTECTION(false, false, false, false, true, true),
	RELOG(false, false, false, false, true, true),
	KILL(false, false, false, false, true, true),
	
	EBOW(false, true, false, false, true, false),
	HAMMER_AOE(true, false, false, false, true, false),
	EVISCERATE(true, false, false, false, true, false),
	NOT_HOLDING_GHOSTBLADE(false, false, false, false, true, false),
	KABOOM(false, false, false, false, true, true),
	;
	
	private final boolean melee;
	private final boolean ranged;
	private final boolean mobImmune;
	private final boolean poison;
	private final boolean custom;
	private final boolean instaKill;
	
	public boolean isMelee() {
		return melee;
	}
	
	public boolean isRanged() {
		return ranged;
	}
	
	public boolean isMobImmune() {
		return mobImmune;
	}
	
	public boolean isPoison() {
		return poison;
	}
	
	public boolean isCustom() {
		return custom;
	}
	
	public boolean isInstaKill() {
		return instaKill;
	}
	
	DamageType(boolean melee, boolean ranged, boolean mobImmune, boolean poison, boolean custom, boolean instaKill) {
		this.melee = melee;
		this.ranged = ranged;
		this.mobImmune = mobImmune;
		this.poison = poison;
		this.custom = custom;
		this.instaKill = instaKill;
	}
}
