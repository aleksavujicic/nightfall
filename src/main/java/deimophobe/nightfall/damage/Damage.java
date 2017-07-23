package deimophobe.nightfall.damage;

import deimophobe.nightfall.GameEntity;
import deimophobe.nightfall.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class Damage<A extends GameEntity, R extends GameEntity> {
	/** The type of damage. */
	private final DamageType type;
	/** The GameEntity which initiated the damage. */
	private final A attacker;
	/** The GameEntity which receives the damage. */
	private final R receiver;
	/** The time which the damage occured. */
	private final long time;
	/** The name of the item which was used to hit. If not applicable this value is null. */
	private final String itemName;
	
	/** How much damage to do */
	private double damage;
	/** If set to true, the damage will no longer occur. Overrides force. */
	private boolean cancelled;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	private final boolean force;
	
	/** True if the final damage has been calculated and applied. No further calculations
	 * should be done if this is true. */
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
	
	public Damage(DamageType type, A attacker, R receiver, double damage) {
		this(type, attacker, receiver, damage, false);
	}
	
	public Damage(DamageType type, A attacker, R receiver, double damage, boolean force) {
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
	
	public void multiplyDamage(double multiplier) {damage *= multiplier;}
	public double getCurrentDamage() {
		return damage;
	}
	
	/**
	 * Gets the damage as in {@link #getCurrentDamage()}, however it should only be called by the event handler.
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
}
