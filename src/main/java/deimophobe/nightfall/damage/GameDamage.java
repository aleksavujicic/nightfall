package deimophobe.nightfall.damage;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 6/05/17.
 */
public abstract class GameDamage<A extends GameEntity, R extends GameEntity> implements CancellableFinalGameDamage<A,R> {
	/** The type of damage. */
	protected final GameDamageType type;
	/** The GameEntity which initiated the damage. */
	protected final A attacker;
	/** The GameEntity which receives the damage. */
	protected final R receiver;
	/** How much damage to do. */
	private MultiPartValue mulitPartDamage;
	
	/** The current phase of the damage. */
	private DamagePhase phase;
	/** The time which the damage occured. */
	private final long time;
	/** The item which was used to hit. If not applicable this value is null. */
	private final ItemStack itemStack;
	
	/** How much knockback to do. */
	protected Vector knockback;
	/** If set to true, the damage will no longer occur. Overrides force. */
	protected boolean cancelled;
	/** If set to true, the damage will not occur, but there will still be a damage tick. */
	protected boolean softCancelled;
	/** If set to true, damage will occur regardless of invincibility ticks. Overrided by force. */
	protected int noDmgTicks;
	/** If set to true, damage will be 'infinite'. */
	protected boolean instaKill;
	
	private final Set<DamageHandler<CancellableFinalGameDamage<A,R>>> preDamageHandlers = new HashSet<>();
	private final Set<DamageHandler<FinalGameDamage<A,R>>> postDamageHandlers = new HashSet<>();
	
	private static int idCount = 0;
	/** Currently only used for debugging */
	private final int ID;
	
	
	// ------ STATIC INITIALISERS -------
	public static <B extends GameEntity,S extends GameEntity> GameDamage<?,?> createDamage(B attacker, S receiver, GameDamageType type, double damage) {
		return createDamage(attacker, receiver, type, damage, null);
	}
	
	static <B extends GameEntity,S extends GameEntity> GameDamage<?,?> createDamage(B attacker, S receiver, GameDamageType type, double damage, Projectile arrow) {
		if (receiver instanceof MonsterEntity) {
			return new MonsterDamage(attacker, (MonsterEntity) receiver, type, damage, arrow);
		} else if (receiver instanceof Dwarf) {
			return new DwarfDamage(attacker, (Dwarf) receiver, type, damage, arrow);
		} else {
			throw new IllegalArgumentException("Game damage must have attacker/receiver be dwarf/monster or monster/dwarf.");
		}
	}
	
	// ------ CONSTRUCTORS -------
	public GameDamage(A attacker, R receiver, GameDamageType type, double damage) {
		this(attacker, receiver, type, damage, null);
	}
	
	protected GameDamage(A attacker, R receiver, GameDamageType type, double damage, Projectile arrow) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.phase = DamagePhase.PRE_FIRE;
		this.time = System.currentTimeMillis();
		this.itemStack = getHeldItemOfDamager(attacker);
		
		this.mulitPartDamage = new MultiPartValue(damage);
		int resLevel = receiver.getPotionEffectLevel(PotionEffectType.DAMAGE_RESISTANCE);
		double res = Math.min(1, 1 - resLevel*0.1);
		mulitPartDamage.timesMult(res);
		
		this.cancelled = false;
		this.softCancelled = false;
		this.noDmgTicks = 8;
		this.instaKill = false;
		
		this.arrow = arrow;
		
		if (type == GameDamageType.MELEE) {
			setKnockbackFromMelee();
		} else if (type == GameDamageType.RANGED) {
			setKnockbackFromArrow();
		} else {
			this.knockback = null;
		}
		
		this.ID = idCount;
		idCount++;
		
		type.applyModifier(this);
	}
	
	private void setKnockbackFromMelee() {
		//if (attacker == null) return;
		
		int knockbackLevel = 0;
		if (itemStack != null) knockbackLevel = itemStack.getEnchantmentLevel(Enchantment.KNOCKBACK);
		
		Vector offset = receiver.offsetFrom(attacker);
		offset.setY(0).normalize().multiply(0.6 + 0.6 * knockbackLevel);
		offset.setY(0.25);
		offset.add(attacker.getVelocity().multiply(0.7));
		
		knockback = offset;
	}
	
	private void setKnockbackFromArrow() {
		Vector offset = arrow.getVelocity();
		
		int punchLevel = 0;
		if (arrow instanceof Arrow) punchLevel = ((Arrow) arrow).getKnockbackStrength();
		
		offset.setY(0).normalize().multiply(0.6 + 0.6 * punchLevel);
		offset.setY(0.25 + 0.05*punchLevel);
		
		knockback = offset;
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
	
	public MultiPartValue getMulitPartDamage() { return mulitPartDamage; }
	
	public void setNoDmgTicks(int ticks) { noDmgTicks = ticks; }
	public void instaKill() {
		instaKill = true;
		cancelled = false;
		softCancelled = false;
	}
	public void cancel() {
		if (!instaKill) {
			cancelled = true;
		}
	}
	public void softCancel() {
		if (!instaKill) {
			softCancelled = true;
		}
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	
	public void setKnockback(double x, double y, double z) {
		setKnockback(new Vector(x,y,z));
	}
	public void setKnockback(Vector kb) {
		knockback = kb;
		knockback.checkFinite();
	}
	public void addKnockback(double x, double y, double z) {
		addKnockback(new Vector(x,y,z));
	}
	public void addKnockback(Vector kb) {
		initialiseKnockbackIfNull();
		knockback.add(kb);
		knockback.checkFinite();
	}
	public void multiplyKnockback(double mult) {
		initialiseKnockbackIfNull();
		knockback.multiply(mult);
		knockback.checkFinite();
	}
	private void initialiseKnockbackIfNull() {
		if (knockback == null) knockback = new Vector(0,0,0);
	}
	private void makeKnockbacFinite() {
		if (knockback == null) return;
		
		if (!NumberConversions.isFinite(knockback.getX())) knockback.setX(0);
		if (!NumberConversions.isFinite(knockback.getY())) knockback.setY(0);
		if (!NumberConversions.isFinite(knockback.getZ())) knockback.setZ(0);
	}
	
	private static final double INSTA_KILL_DMG = 1000000;
	public double getFinalDamage() {
		if (instaKill) return INSTA_KILL_DMG;
		if (softCancelled || cancelled) return 0;
		
		return mulitPartDamage.getValue();
	}
	
	public boolean willKill() {
		return (receiver.getHealth() - getFinalDamage() <= 0.000001 || instaKill);
	}
	
	private final Projectile arrow;
	public boolean hasArrow() {return  arrow instanceof Arrow;}
	public Arrow getArrow() {
		if (arrow instanceof Arrow)
			return (Arrow) arrow;
		else
			throw new IllegalStateException("Tried to access arrow of gameDamage which has no arrow.");
	}
	
	
	public static int SAFETY_JUICE_PRIORITY = 10;
	public static int RESURRECTION_PRIORITY = 100;
	public static int ARTHEA_DEATH_PRIORITY = 200;
	public void addPreDamageHandler(Consumer<CancellableFinalGameDamage<A,R>> handler) {
		addPreDamageHandler(0, handler);
	}
	public void addPreDamageHandler(int priority, Consumer<CancellableFinalGameDamage<A,R>> handler) {
		preDamageHandlers.add(new DamageHandler<>(priority, handler));
	}
	
	public void addPostDamageHandler(Consumer<FinalGameDamage<A,R>> handler) {
		addPostDamageHandler(0, handler);
	}
	public void addPostDamageHandler(int priority, Consumer<FinalGameDamage<A,R>> handler) {
		postDamageHandlers.add(new DamageHandler<>(priority, handler));
	}
	
	/** Fires a custom damage event */
	public void fire() {
		fire(false);
	}
	public void fire(boolean force) {
		DamageUtil.fireDamage(this, force);
	}
	
	/** Fires a custom damage event */
	void onFire(boolean force) {
		// Check that damage has not already been fired
		if (phase != DamagePhase.PRE_FIRE) throw new IllegalStateException("Already fired damage: " + this);
		
		// Check if damage is allowed to occur by game ticks
		if (!force && receiver.getEntity().getNoDamageTicks() != 0) return;
		
		
		// Notify attacker and receiver, and let them set up their events
		phase = DamagePhase.NOTIFYING;
		notifyEntities();
		if (cancelled) return;
		
		
		// Apply pre damage handlers, stop if necessary
		phase = DamagePhase.PRE_DAMAGE;
		for (DamageHandler<CancellableFinalGameDamage<A, R>> handler : Misc.asSortedList(preDamageHandlers)) {
			handler.consume(this);
			if (cancelled) return;
		}
		
		
		// Do the damage
		LivingEntity receiverEntity = receiver.getEntity();
		
		phase = DamagePhase.DAMAGING;
		double doDamageAmt = getFinalDamage();
		if (doDamageAmt == 0) {
			doDamageAmt = 100;
			softCancel();
		}
		
		receiverEntity.setNoDamageTicks(0);
		DamageUtil.processingDamage = this;
		receiverEntity.damage(doDamageAmt);
		DamageUtil.processingDamage = null;
		receiverEntity.setNoDamageTicks(noDmgTicks);
		
		// Apply knockback
		if (knockback != null) {
			double kbResist = receiverEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
			knockback.multiply(1 - kbResist);
			receiver.setVelocity(knockback);
		}
		
		// Notify gamePlayer to save damage
		if (receiver instanceof GamePlayer) {
			DamageOccurance occurance = new DamageOccurance(attacker, receiver, type, time, "temp"); //itemStack.getItemMeta().getDisplayName());
			((GamePlayer) receiver).notifyDamage(occurance);
		}
		
		
		// Apply post damage handlers
		phase = DamagePhase.POST_DAMAGE;
		Misc.asSortedList(postDamageHandlers).forEach(h -> h.consume(this));
		
		// Debug
		if (attacker instanceof GamePlayer) ((GamePlayer) attacker).debugObject(this);
		if (receiver instanceof GamePlayer) ((GamePlayer) receiver).debugObject(this);
	}
	
	abstract void notifyEntities();
	
	/** Not really used directly, but is useful as a guideline */
	public enum DamagePhase {
		/** Used for setting up base values. */
		PRE_FIRE,
		/** Used for altering values and setting up future events. */
		NOTIFYING,
		/** Used to prevent damage, based on damage. */
		PRE_DAMAGE,
		/** When all the damage stuff is occurring. Currently serves no purpose. */
		DAMAGING,
		/** Used to monitor the outcome of the damage. */
		POST_DAMAGE
	}
	
	
	// ------ STATIC HELPERS -------
	
	private static ItemStack getHeldItemOfDamager(GameEntity damager) {
		if (!(damager instanceof GamePlayer)) return null;
		
		GamePlayer gp = ((GamePlayer) damager);
		ItemStack item = gp.getHeldItem();
		if (item == null) return null;
		
		return item;
	}
	
	/// ----- DEBUG ------
	
	@Override
	public String toString() {
		StringBuilder extraString = new StringBuilder();
		
		if (instaKill)
			extraString.append("Instakill, ");
		
		if (cancelled)
			extraString.append("Cancelled, ");
		
		if (softCancelled)
			extraString.append("Soft Cancelled, ");
		
		if (hasArrow())
			extraString.append("Has Arrow, ");
		
		if (extraString.length() > 0)
			extraString.setLength(extraString.length() - 2);
		
		String attackerName = (attacker == null ? "NONE" : attacker.getName());
		
		DecimalFormat df = new DecimalFormat("#.####");
		
		return "GameDamage ID" + ID + " at " + time + " from " + attackerName + ChatColor.RESET + " to " + receiver.getName() + ChatColor.RESET + " of type: " + type + ".\n"
				+ "  DAMAGES - " + mulitPartDamage.toString() + "\n"
				+ (knockback != null ? "  Knockback: " + df.format(knockback.length()) + "\n" : "")
				+ "  NoDmgTicks: " + noDmgTicks + ".\n"
				+ "  Pre Handlers: " + preDamageHandlers.size() + "; " + "Post Handlers: " + postDamageHandlers.size() + "\n"
				+ (extraString.length() > 0 ? "  EXTRA - " + extraString.toString() + "\n" : "");
	}
}
