package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/05/17.
 */
public class GameDamage<A extends GameEntity, R extends GameEntity> {
	/** The event which caused this game damage. */
	private final EntityDamageEvent event;
	
	/** The type of damage. */
	private final GameDamageType type;
	/** The GameEntity which initiated the damage. */
	private final A attacker;
	/** The GameEntity which receives the damage. */
	private final R receiver;
	/** How much damage to do. */
	private double damage;
	/** How much knockback to do. */
	private Vector knockback;
	
	/** The time which the damage occured. */
	private final long time;
	/** The name of the item which was used to hit. If not applicable this value is null. */
	private final String itemName;
	/** If set to true, the damage will no longer occur. Overrides force. */
	private boolean cancelled;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	private final boolean force;
	
	
	private final ArrowDamageData arrowData;
	public ArrowDamageData arrowData() {return arrowData;}
	
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
	
	public GameDamage(EntityDamageEvent event, GameDamageType type, A attacker, R receiver, double damage, boolean force, Projectile arrow) {
		this.event = event;
		
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		this.force = force;
		
		this.time = GameMap.getCurrentMap().getWorld().getTime();
		this.itemName = getHeldItemOfDamager(attacker);
		
		this.damage = damage;
		this.cancelled = false;
		
		this.arrowData = ((arrow != null) ? new ArrowDamageData(arrow) : null);
	}
	
	public GameDamageType getType() {
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
	
	
	
	// =================
	// DAMAGE PROCESSING
	// =================
	
	public static GameDamage<?, ?> createDamageFromEvent(EntityDamageEvent event) {
		GameEntity damagee = Game.getGame().getGameEntity(event.getEntity());
		NaturalDamageType type = null;
		GameEntity damager = null;
		
		switch (event.getCause()) {
			case STARVATION:
			case SUFFOCATION:
			default:
				throw new IllegalArgumentException("Cannot create GameDamage with event cause " + event.getCause());
			
			case ENTITY_ATTACK:
				damager = Game.getGame().getGameEntity( ((EntityDamageByEntityEvent) event).getDamager() );
				return new GameDamage<>(event, NaturalDamageType.MELEE, damager, damagee, event.getDamage(), false, null);
			
			case PROJECTILE:
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				damager = Game.getGame().getGameEntity((Entity) proj.getShooter());
				return new GameDamage<>(event, NaturalDamageType.MELEE, damager, damagee, event.getDamage(), false, proj);
			
			case CONTACT: type = NaturalDamageType.CONTACT; break;
			case DROWNING: type = NaturalDamageType.DROWNING; break;
			case HOT_FLOOR: type = NaturalDamageType.MAGMA_BLOCK; break;
			case FALL: type = NaturalDamageType.FALL; break;
			case LAVA: type = NaturalDamageType.LAVA; break;
			
			case FIRE:
			case FIRE_TICK:
				type = NaturalDamageType.FIRE;
				break;
			
			case POISON:
			case WITHER:
				type = NaturalDamageType.POISON;
				break;
			
			case VOID:
				type = NaturalDamageType.VOID;
				break;
		}
		
		switch (type) {
			case CONTACT:
				break;
			case DROWNING:
				break;
			case FIRE:
				break;
			case LAVA:
				break;
			case MAGMA_BLOCK:
				break;
			case FALL:
				break;
			case VOID:
				break;
			case POISON:
				break;
		}
		return null;
	}
	
	//public static
}
