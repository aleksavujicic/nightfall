package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.entity.Arrow;
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
	private double baseDamage;
	
	/** The time which the damage occured. */
	private final long time;
	/** The name of the item which was used to hit. If not applicable this value is null. */
	private final String itemName;
	
	/** The amount the damage will be boosted by. */
	private double damageBooster = 0;
	/** The amount the damage will be multiplied by. */
	private double damageMultiplier = 1;
	
	/** How much knockback to do. */
	protected Vector knockback;
	/** If set to true, the damage will no longer occur. Overrides force. */
	protected boolean cancelled;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	protected boolean force;
	/** If set to true, damage will be 'infinite'. */
	protected boolean instaKill;
	
	
	private final Projectile arrow;
	public boolean hasArrow() {return  arrow instanceof Arrow;}
	public Arrow getArrow() {
		if (arrow instanceof Arrow)
			return (Arrow) arrow;
		else
			throw new IllegalStateException("Tried to access arrow of game damage which has no arrow.");
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
		if (receiver instanceof MonsterEntity) {
			return new MonsterDamage<>(attacker, (MonsterEntity) receiver, type, damage, arrow);
		} else if (receiver instanceof Dwarf) {
			return new DwarfDamage<>(attacker, (Dwarf) receiver, type, damage, arrow);
		} else {
			throw new IllegalArgumentException("Game damage must have attacker/receiver be dwarf/monster or monster/dwarf.");
		}
	}
	
	protected GameDamage(A attacker, R receiver, GameDamageType type, double baseDamage, Projectile arrow) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.time = GameMap.getCurrentMap().getWorld().getTime();
		this.itemName = getHeldItemOfDamager(attacker);
		
		this.baseDamage = baseDamage;
		this.knockback = null;
		
		this.cancelled = false;
		this.force = false;
		this.instaKill = false;
		
		this.arrow = arrow;
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
	public void softCancel() {
		baseDamage = 0;
		damageBooster = 0;
		damageMultiplier = 0;
	}
	
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
	
	public void setBaseDamage(double dmg) {this.baseDamage = dmg;}
	public void setBooster(double amt) {this.damageBooster = amt;}
	public void addBooster(double amt) {this.damageBooster += amt;}
	public void setMultiplier(double amt) {this.damageMultiplier = amt;}
	public void addMultiplier(double amt) {this.damageMultiplier += amt;}
	public void timesMultiplier(double amt) {this.damageMultiplier *= amt;}
	
	public double getFinalDamage() {
		if (instaKill) {
			return INSTA_KILL_DMG;
		} else if (cancelled) {
			return 0;
		} else  {
			return (baseDamage + damageBooster) * damageMultiplier;
		}
	}
	
	public boolean willKill() {
		return (receiver.getHealth() - getFinalDamage() <= 0.1 || instaKill);
	}
	
	abstract void notifyEntities();
	
	private static final double INSTA_KILL_DMG = 100000;
	boolean applyDamage(EntityDamageEvent event) {
		if (applied) throw new IllegalStateException("Attempted to get final damage even though already accessed.");
		
		
		boolean successful = true;
		// Calculate damage
		// Priority: insta > cancelled > force > none ?
		if (instaKill) {
			event.setCancelled(false);
		} else if (cancelled) {
			event.setCancelled(true);
			successful = false;
		}
		event.setDamage(getFinalDamage());
		
		if (successful && knockback != null)
			receiver.setVelocity(knockback);
		
		applied = true;
		
		return successful;
	}
	
	@Override
	public String toString() {
		StringBuilder extraString = new StringBuilder();
		
		if (instaKill)
			extraString.append("Instakill, ");
		
		if (cancelled)
			extraString.append("Cancelled, ");
		
		if (force)
			extraString.append("Forced, ");
		
		if (hasArrow())
			extraString.append("Has Arrow, ");
		
		if (instaKill || cancelled || force || hasArrow())
			extraString.setLength(extraString.length() - 2);
		
		String attackerName = (attacker == null ? "NONE" : attacker.getName());
		
		return "GameDamage at " + time + " from " + attackerName + " to " + receiver.getName() + " of type: " + type + ". "
				+ "DAMAGES - Base: " + baseDamage + " Boost: " + damageBooster + " Mult: " + damageMultiplier + ". "
				+ (knockback != null ? "Knockback: " + knockback.length() + ". " : "")
				+ "EXTRA - " + extraString.toString() + ". ";
	}
}
