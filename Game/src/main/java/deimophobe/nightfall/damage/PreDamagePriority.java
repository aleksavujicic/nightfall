package deimophobe.nightfall.damage;

/**
 * Created by Deimophobe on 22/03/18.
 */
public enum PreDamagePriority {
	DWARF_BUILD_PHASE_SAVER,
	DEFAULT,
	SAFETY_JUICE,
	JIT_HEAL,
	RESURRECTION,
	ARTHEA_DEATH,
	MONSTER_DEATH,
	
	ASSASSIN_KILL, // Should ideally be a post damage handler
}
