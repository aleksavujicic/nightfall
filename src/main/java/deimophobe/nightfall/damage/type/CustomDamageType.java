package deimophobe.nightfall.damage.type;

/**
 * Created by Deimophobe on 29/08/17.
 */
public enum CustomDamageType implements GameDamageType {
	// Mob damage
	SEPPUKU,
	SHRINE_PROTECTION,
	SELF_GOBO_KABOOM,
	
	EBOW,
	EVISCERATE,
	HAMMER_AOE,
	INCORRECT_HELD_ITEM,
	TINDERFLAME,
	WILDFIRE,
	
	AI_REMOVER,
	
	// Dwarf damage
	DEATH_PLAGUE,
	GOBO_KABOOM,
	GOBO_BOX_EXPLOSION,
	MOBSPAWN,
	
	MINOTAUR_CHARGE,
	WRAITH_CHARGE,
	
	BLOOD_MAGIC, // Current for arthea's teleport
	
	
	// Misc
	COMMAND,
	MISC_EXPLOSION,
	VOLCANIC_BOW,
	@Deprecated TEMPORARY
}
