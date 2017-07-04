package deimophobe.dvz.damage;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class DamageEvent<A extends GameEntity, R extends GameEntity> {
	private final DamageType type;
	private final A attacker;
	private final R receiver;
	private final long time;
	private final String itemName;
	
	private double damage;
	private boolean cancelled;
	private final boolean force;
	
	private boolean calculated = false;
	
	private static String getHeldItemOfDamager(GameEntity damager) {
		if (!(damager instanceof GamePlayer)) return null;
		
		GamePlayer gp = ((GamePlayer) damager);
		ItemStack item = gp.getHeldItem();
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		return meta.getDisplayName();
	}
	
	public DamageEvent(DamageType type, A attacker, R receiver, double damage) {
		this(type, attacker, receiver, damage, false);
	}
	
	public DamageEvent(DamageType type, A attacker, R receiver, double damage, boolean force) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		this.force = force;
		
		this.time = GameMap.getCurrentMap().getWorld().getTime();
		this.itemName = getHeldItemOfDamager(attacker);
		
		this.damage = damage;
		this.cancelled = false;
	}
	
	public DamageType getType() {
		return type;
	}
	
	public A getAttacker() {
		return attacker;
	}
	
	public R getReceiver() {
		return receiver;
	}
	
	public void cancel() {
		cancelled = true;
	}
	
	boolean isCancelled() { return cancelled; }
	
	public void multiplyDamage(double multiplier) {
		damage *= multiplier;
	}
	
	public double getDamage() {
		return damage;
	}
	
	/**
	 * Gets the damage as in {@link #getDamage()}, however it should only be called by the event handler.
	 * Marks any future attempt at getting the damage as invalid, and any attempt to do so will throw an
	 * {@link IllegalStateException}.
	 *
	 * One should also check that the event has not been cancelled as this will also throw an {@link IllegalStateException}.
	 *
	 * @return The final damage of the event.
	 * @throws IllegalStateException If the event has been cancelled or has already been called.
	 */
	double getFinalDamage() {
		if (calculated) throw new IllegalStateException("Attempted to get final damage even though already accessed.");
		if (cancelled) throw new IllegalStateException("Attempted to get final damage but the event has been cancelled.");
		
		calculated = true;
		return damage;
	}
	
	public String generateDeathMessage() {
		if (!calculated) throw new IllegalStateException("Attempted to generate death message even but the event hasn't finished.");
		if (cancelled) throw new IllegalStateException("Attempted to generate death message but the event has been cancelled.");
		
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
		
	}
}
