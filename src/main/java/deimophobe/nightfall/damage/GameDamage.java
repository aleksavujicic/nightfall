package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

/**
 * Created by Deimophobe on 6/05/17.
 */
public abstract class GameDamage<A extends GameEntity, R extends GameEntity> {
	/** The type of damage. */
	protected final GameDamageType type;
	/** The GameEntity which initiated the damage. */
	protected final A attacker;
	/** The GameEntity which receives the damage. */
	protected final R receiver;
	/** How much damage to do. */
	
	/** The time which the damage occured. */
	private final long time;
	/** The name of the item which was used to hit. If not applicable this value is null. */
	private final String itemName;
	
	protected double damage;
	/** How much knockback to do. */
	protected Vector knockback;
	/** If set to true, the damage will no longer occur. Overrides force. */
	protected boolean cancelled;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	protected boolean force;
	/** If set to true, damage will be 'infinite'. */
	protected boolean instaKill;
	
	
	private final ArrowDamageData arrowData;
	public boolean hasArrowData() {return  arrowData != null;}
	public ArrowDamageData arrowData() {
		if (arrowData == null) throw new IllegalStateException("Tried to access arrow data of game damage which has no arrow.");
		return arrowData;
	}
	
	/** True if the final damage has been applied and applied. No further calculations
	 * should be done if this is true. */
	private boolean applied = false;
	
	private static String getHeldItemOfDamager(GameEntity damager) {
		if (!(damager instanceof GamePlayer)) return null;
		
		GamePlayer gp = ((GamePlayer) damager);
		ItemStack item = gp.getHeldItem();
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		return meta.getDisplayName();
	}
	
	
	
	static <B extends GameEntity,S extends GameEntity> GameDamage createDamage(B attacker, S receiver, GameDamageType type, double damage) {
		return createDamage(attacker, receiver, type, damage, null);
	}
	
	
	static <B extends GameEntity,S extends GameEntity> GameDamage createDamage(B attacker, S receiver, GameDamageType type, double damage, Projectile arrow) {
		if ((attacker == null || attacker instanceof Dwarf) && receiver instanceof MonsterEntity) {
			return new MonsterDamage((Dwarf) attacker, (MonsterEntity) receiver, type, damage, arrow);
		} else if ((attacker == null || attacker instanceof MonsterEntity) && receiver instanceof Dwarf) {
			return new DwarfDamage((MonsterEntity) attacker, (Dwarf) receiver, type, damage, arrow);
		} else {
			throw new IllegalArgumentException("Game damage must have attacker/receiver be dwarf/monster or monster/dwarf.");
		}
	}
	
	protected GameDamage(A attacker, R receiver, GameDamageType type, double damage, Projectile arrow) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.time = GameMap.getCurrentMap().getWorld().getTime();
		this.itemName = getHeldItemOfDamager(attacker);
		
		this.damage = damage;
		
		this.knockback = null;
		
		this.cancelled = false;
		this.force = false;
		this.instaKill = false;
		
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
	
	public void cancel() {cancelled = true;}
	public void force() {force = true;}
	public void instaKill() {instaKill = true;}
	
	public void setKnockback(Vector kb) {knockback = kb;}
	private void checkKBNotNull() {
		if (knockback == null) knockback = new Vector(0,0,0);
	}
	public void addKnockback(Vector kb) {
		checkKBNotNull();
		knockback.add(kb);
	}
	public void multiplyKnockback(double mult) {
		checkKBNotNull();
		knockback.multiply(mult);
	}
	
	public void setDamage(double damage) {this.damage = damage;}
	public void addDamage(double damage) {this.damage += damage;}
	public void multiplyDamage(double multiplier) {damage *= multiplier;}
	public double getCurrentDamage() {
		return damage;
	}
	
	abstract void notifyEntities();
	
	private static final double INSTA_KILL_DMG = 100000;
	boolean applyDamage(EntityDamageEvent event) {
		if (applied) throw new IllegalStateException("Attempted to get final damage even though already accessed.");
		
		if (hasArrowData()) {
			Projectile arrow = arrowData.getArrow();
			if (arrow.hasMetadata("force")) {
				damage *= arrow.getMetadata("force").get(0).asDouble();
			} else {
				Bukkit.getLogger().warning("Arrow has no attached force?");
			}
		}
		
		boolean successful = true;
		// Calculate damage
		// Priority: insta > cancelled > force > none ?
		if (instaKill) {
			event.setDamage(INSTA_KILL_DMG);
			event.setCancelled(false);
		} else if (cancelled) {
			event.setDamage(0);
			event.setCancelled(true);
			successful = false;
		} else  {
			event.setDamage(damage);
			// TODO
		}
		
		if (successful && knockback != null)
			receiver.setVelocity(knockback);
		
		applied = true;
		
		return successful;
	}
}
