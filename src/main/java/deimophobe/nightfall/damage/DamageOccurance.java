package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.entity.GameEntity;

/**
 * Created by Deimophobe on 17/07/17.
 */
public class DamageOccurance  { //implements Comparable<DamageOccurance> {
	private final GameEntity attacker;
	private final GameEntity receiver;
	private final GameDamageType type;
	private final long time;
	private final String itemName;
	
	public DamageOccurance(GameEntity attacker, GameEntity receiver, GameDamageType type, long time, String itemName) {
		this.attacker = attacker;
		this.receiver = receiver;
		this.type = type;
		this.time = time;
		this.itemName = itemName;
	}
	
	public String generateDeathMessage() {
		/*
		String name = receiver.getDisplayName();
		
		if (type == null) return name + " died.";
		
		String killMsg;
		
		switch (type) {
			case HAMMER_AOE:
			case REGULAR_MELEE:
				killMsg = "slain";
				break;
			case REGULAR_RANGED:
				killMsg = "shot";
				break;
			case EBOW:
				killMsg = "pierced";
				break;
			case EVISCERATE:
				killMsg = "eviscerated";
				break;
			case WILDFIRE:
				killMsg = "incinerated";
				break;
			case TINDERFLAME:
				killMsg = "zooped";
				break;
			
			case POISON:
				return name + " withered away.";
			
			
			case CONTACT:
				return name + " was pricked to death.";
			case DROWNING:
				return name + " drowned.";
			case FALL:
				return name + " fell to their doom.";
			case HOT_FLOOR:
				return name + " burnt their feet.";
			case CRAMMING:
				return name + " was crushed.";
			case FALLING_BLOCK:
				return name + " was squished.";
			case LIGHTNING:
				return name + " angered the gods.";
			case LAVA:
				return name + " tried to swim in lava.";
			case FIRE:
				return name + " couldn't find water.";
			
			case NOT_HOLDING_GHOSTBLADE:
				return name + " was a bit of a klutz and dropped their blade.";
			
			case VOID:
				return name + " was swallowed by the abyss.";
			case SEPPUKU:
				return name + " committed sudoku.";
			case SHRINE_PROTECTION:
				return name + " was zapped by lightning.";
			case RELOG:
				return name + " combat logged.";
			case KABOOM:
				return name + " went kaboom.";
			case DEATH_PLAGUE:
				return name + " was touched by " + ChatColor.BLACK + "DEATH" + ChatColor.RESET + ".";
			case MOBSPAWN:
				return name + " was consumed by the source of the darkness.";
			
			default:
				return name + " died.";
		}
		
		if (attacker == null)
			return name + " was " + killMsg + ".";
		
		String damagerName = attacker.getDisplayName();
		if (itemName != null)
			return name + " was " + killMsg + " by " + damagerName + " using " + itemName + ".";
		else
			return name + " was " + killMsg + " by " + damagerName + ".";
		*/
		return null;
	}
	
	/*
	@Override
	public int compareTo(DamageOccurance occurance) {
		int compAt = compareAttacker(occurance.attacker);
		
		if (compAt == 0) {
			retur
		} else {
			return compAt;
		}
	}
	
	private boolean shouldForceOverride(GameEntity newAttacker) {
		if (newAttacker == null && attacker == null)
			return 0;
		if (newAttacker == null)
			return 1;
		if (attacker == null)
			return -1;
		
		if (newAttacker instanceof AIEntity && attacker instanceof AIEntity)
			return 0;
		if (newAttacker instanceof AIEntity)
			return 1;
		if (attacker instanceof AIEntity)
			return -1;
		
		if (newAttacker instanceof GamePlayer && attacker instanceof GamePlayer)
			return 0;
		
		if (newAttacker instanceof GamePlayer)
			throw new IllegalStateException("Invalid GameEntity type of attacker when comparing: " + attacker);
		else
			throw new IllegalArgumentException("Invalid GameEntity type of newAttacker when comparing: " + newAttacker);
	}
	*/
}
