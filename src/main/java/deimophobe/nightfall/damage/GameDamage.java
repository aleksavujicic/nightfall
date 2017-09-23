package deimophobe.nightfall.damage;

import deimophobe.nightfall.damage.type.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
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
	/** The type of getDamage. */
	protected final GameDamageType type;
	/** The GameEntity which initiated the getDamage. */
	protected final A attacker;
	/** The GameEntity which receives the getDamage. */
	protected final R receiver;
	/** How much getDamage to do. */
	private MultiPartValue damage;
	
	/** The time which the getDamage occured. */
	private final long time;
	/** The name of the item which was used to hit. If not applicable this value is null. */
	private final String itemName;
	
	/** How much knockback to do. */
	protected Vector knockback;
	/** If set to true, the getDamage will no longer occur. Overrides force. */
	protected boolean cancelled;
	/** If set to true, getDamage will occur regardless of invincibility ticks. Overrided by force. */
	protected int noDmgTicks;
	/** If set to true, getDamage will be 'infinite'. */
	protected boolean instaKill;
	
	
	private static int idCount = 0;
	private final int ID;
	
	
	private final Projectile arrow;
	public boolean hasArrow() {return  arrow instanceof Arrow;}
	public Arrow getArrow() {
		if (arrow instanceof Arrow)
			return (Arrow) arrow;
		else
			throw new IllegalStateException("Tried to access arrow of game getDamage which has no arrow.");
	}
	
	/** True if the final getDamage has been applied and applied. No further calculations
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
	
	public static <B extends GameEntity,S extends GameEntity> GameDamage createDamage(B attacker, S receiver, GameDamageType type, double damage) {
		return createDamage(attacker, receiver, type, damage, null);
	}
	
	
	static <B extends GameEntity,S extends GameEntity> GameDamage createDamage(B attacker, S receiver, GameDamageType type, double damage, Projectile arrow) {
		if (receiver instanceof MonsterEntity) {
			return new MonsterDamage(attacker, (MonsterEntity) receiver, type, damage, arrow);
		} else if (receiver instanceof Dwarf) {
			return new DwarfDamage(attacker, (Dwarf) receiver, type, damage, arrow);
		} else {
			throw new IllegalArgumentException("Game getDamage must have attacker/receiver be dwarf/monster or monster/dwarf.");
		}
	}
	
	public GameDamage(A attacker, R receiver, GameDamageType type, double damage) {
		this(attacker, receiver, type, damage, null);
	}
	
	protected GameDamage(A attacker, R receiver, GameDamageType type, double damage, Projectile arrow) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.time = System.currentTimeMillis();
		this.itemName = getHeldItemOfDamager(attacker);
		
		this.damage = new MultiPartValue(damage);
		this.knockback = null;
		
		this.cancelled = false;
		this.noDmgTicks = receiver.getEntity().getMaximumNoDamageTicks();
		this.instaKill = false;
		
		this.arrow = arrow;
		
		this.ID = idCount;
		idCount++;
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
	
	public MultiPartValue getDamage() { return damage; }
	
	public void setNoDmgTicks(int ticks) { noDmgTicks = ticks; }
	public void cancel() {cancelled = true;}
	public void instaKill() {instaKill = true;}
	
	public void softCancel() {
		damage.setBase(0);
		damage.setBoost(0);
		damage.setMultiplier(0);
	}
	
	public void setKnockback(Vector kb) {knockback = kb;}
	public void setKnockback(double x, double y, double z) {setKnockback(new Vector(x,y,z));}
	private void checkKBNotNull() {
		if (knockback == null) knockback = new Vector(0,0,0);
	}
	public void addKnockback(Vector kb) {
		checkKBNotNull();
		knockback.add(kb);
	}
	public void addKnockback(double x, double y, double z) {addKnockback(new Vector(x,y,z));}
	public void multiplyKnockback(double mult) {
		checkKBNotNull();
		knockback.multiply(mult);
	}
	
	public double getFinalDamage() {
		if (instaKill) {
			return INSTA_KILL_DMG;
		} else if (cancelled) {
			return 0;
		} else  {
			return damage.getValue();
		}
	}
	
	public boolean willKill() {
		return (receiver.getHealth() - getFinalDamage() <= 0.1 || instaKill);
	}
	
	abstract void notifyEntities();
	
	private static final double INSTA_KILL_DMG = 100000;
	boolean applyDamage(EntityDamageEvent event) {
		if (applied) throw new IllegalStateException("Attempted to get final getDamage even though already accessed.");
		
		
		boolean successful = true;
		// Calculate getDamage
		// Priority: insta > cancelled > none ?
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
		
		if (receiver instanceof GamePlayer) {
			DamageOccurance occurance = new DamageOccurance(attacker, receiver, type, time, itemName);
			((GamePlayer) receiver).notifyDamage(occurance);
		}
		
		return successful;
	}
	
	@Override
	public String toString() {
		StringBuilder extraString = new StringBuilder();
		
		if (instaKill)
			extraString.append("Instakill, ");
		
		if (cancelled)
			extraString.append("Cancelled, ");
		
		if (hasArrow())
			extraString.append("Has Arrow, ");
		
		if (extraString.length() > 0)
			extraString.setLength(extraString.length() - 2);
		
		String attackerName = (attacker == null ? "NONE" : attacker.getName());
		
		return "GameDamage ID" + ID + " at " + time + " from " + attackerName + " to " + receiver.getName() + " of type: " + type + ". "
				+ "DAMAGES - " + damage.toString() + ". "
				+ (knockback != null ? "Knockback: " + knockback.length() + ". " : "")
				+ "NoDmgTicks: " + noDmgTicks + ". "
				+ "EXTRA - " + extraString.toString() + ". ";
	}
}
