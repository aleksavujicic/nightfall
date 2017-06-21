package deimophobe.dvz;

/**
 * Created by Deimophobe on 20/01/17.
 */
public enum DamageType {
	REGULAR_MELEE(true, false),
	REGULAR_RANGED(false, true),
	
	GENERIC_MAGIC(false, false),
	
	POISON(10, 5),
	
	EXPLOSION(-1, -1),
	
	CONTACT(2, -1),
	DROWNING(4, -1),
	HOT_FLOOR(2, -1),
	CRAMMING(3, -1),
	FALLING_BLOCK(3, -1),
	LIGHTNING(20, -1),
	LAVA(10, -1, 20),
	FIRE(5, -1, 2),
	
	FALL(2, -1) {
		@Override
		public double getDwarfDamage(double damage) {
			return 2*(1 - Math.pow(Math.random(),2)/2)*damage;
		}
	},
	
	VOID(true),
	SEPPUKU(true),
	SHRINE_PROTECTION(true),
	MOBSPAWN(false, false),
	RELOG(true),
	DEATH_PLAGUE(true),
	KILL(true),
	AI_REMOVAL(true),
	
	EBOW(true, true),
	HAMMER_AOE(false, false),
	EVISCERATE(true, false),
	NOT_HOLDING_GHOSTBLADE(false, false),
	GOBO_BOX(false, false),
	KABOOM(false, false),
	
	WILDFIRE(true, false),
	TINDERFLAME(false, false),
	
	;
	
	private final boolean instaKill;
	private final double dwarfMult;
	private final double mobMult;
	private final int dwarfArmourDmg;
	
	private final boolean proccable;
	private final boolean arrow;
	
	public boolean isInstaKill() {
		return instaKill;
	}
	
	public double getDwarfDamage(double damage) {
		if (dwarfMult == -1) return -1;
		return dwarfMult*damage;
	}
	public double getMobDamage(double damage) {
		if (mobMult == -1) return -1;
		return mobMult*damage;
	}
	
	public int getDwarfArmourDmg() {
		return dwarfArmourDmg;
	}
	
	public boolean isProccable() {
		return proccable;
	}
	
	public boolean isArrow() {
		return arrow;
	}
	
	public boolean doesOverwriteAttacker() {
		return (this != POISON);
	}
	
	
	
	DamageType(boolean proccable, boolean arrow) {
		this.instaKill = false;
		
		this.dwarfMult = 1;
		this.mobMult = 1;
		this.dwarfArmourDmg = 0;
		
		this.proccable = proccable;
		this.arrow = arrow;
	}
	
	DamageType(boolean instaKill) {
		this.instaKill = instaKill;
		
		this.dwarfMult = 1;
		this.mobMult = 1;
		this.dwarfArmourDmg = 0;
		
		this.proccable = false;
		this.arrow = false;
	}
	
	DamageType(double dwarfMult, double mobMult) {
		this(dwarfMult, mobMult, 1);
	}
	
	DamageType(double dwarfMult, double mobMult, int dwarfArmourDmg) {
		this.instaKill = false;
		
		this.dwarfMult = dwarfMult;
		this.mobMult = mobMult;
		this.dwarfArmourDmg = dwarfArmourDmg;
		
		this.proccable = false;
		this.arrow = false;
	}
}
