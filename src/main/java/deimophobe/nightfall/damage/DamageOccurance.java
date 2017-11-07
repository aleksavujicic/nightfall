package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.ChatColor;

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
	
	public String getDeathMessage() {
		String name = receiver.getDisplayName();
		
		if (type == null) return name + " died.";
		
		String killMsg = null;
		
		if (type instanceof NaturalDamageType) {
			switch ((NaturalDamageType) type) {
				case MELEE:
					killMsg = "slain";
					break;
				case RANGED:
					killMsg = "shot";
					break;
						
				case CONTACT:
					return name + " was pricked to death.";
				case DROWNING:
					return name + " drowned.";
				case FALL:
					return name + " fell to their doom.";
				case MAGMA_BLOCK:
					return name + " burnt their feet.";
				case LAVA:
					return name + " tried to swim in lava.";
				case FIRE:
					return name + " couldn't find water.";
				
				case VOID:
					return name + " was swallowed by the abyss.";
				
				case POISON:
					return name + " withered away.";
			}
		}

		if (type instanceof CustomDamageType) {
			switch ((CustomDamageType) type) {
				case HAMMER_AOE:
					killMsg = "slain";
					break;
				case EBOW:
				case LUMINOUS:
					killMsg = "pierced";
					break;
				case VOLCANIC_BOW:
					killMsg = "scorched";
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
				
				case WRAITH_CHARGE:
					killMsg = "drained";
					break;
				case MINOTAUR_CHARGE:
					killMsg = "trampled";
					break;
					
				case GOBO_BOX_EXPLOSION:
				case GOBO_KABOOM:
					killMsg = "exploded";
					break;

				case BLAZE_EXPLOSION:
					killMsg = "blasted";
					break;

				case HUSK_STOMP:
					killMsg = "stomped";
					break;
				
				case INCORRECT_HELD_ITEM:
					return name + " was a bit of a klutz and dropped their blade.";
				case SEPPUKU:
					return name + " committed sudoku.";
				case SHRINE_PROTECTION:
					return name + " was zapped by lightning.";
				case SELF_GOBO_KABOOM:
					return name + " went kaboom.";
				case DEATH_PLAGUE:
					return name + " was touched by " + ChatColor.BLACK + "DEATH" + ChatColor.RESET + ".";
				case MOBSPAWN:
					return name + " was consumed by the source of the darkness.";
				case MISC_EXPLOSION:
					return name + " was pushed to death.";


				default:
					return name + " died.";
			}
		}
		
		if (attacker == null)
			return name + " was " + killMsg + ".";

		String damagerName = attacker.getDisplayName();
		if (itemName != null)
			return name + " was " + killMsg + " by " + damagerName + " using " + itemName + ".";
		else
			return name + " was " + killMsg + " by " + damagerName + ".";
	}
	
	private static final int MAX_LIFETIME = 10*1000;
	public boolean shoulReplace(DamageOccurance occurance) {
		if (occurance == null) return true;
		
		if (time < occurance.time) throw new IllegalArgumentException("shouldReplace should only be called on previous events.\n" +
				"New time: " + time + " Existing time: " + occurance.time);
			
		// Return true if old even expired
		if (time > occurance.time + MAX_LIFETIME) return true;
		
		// Return true if no attacker on old...
		if (occurance.attacker == null) return true;
		// But return false if its not null and new is null
		if (attacker == null) return false;
		
		// Always replace AIEntity first
		if (occurance.attacker instanceof AIEntity) return true;
		// But not allow it to replace others
		if (attacker instanceof AIEntity) return false;
		
		return true;
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
