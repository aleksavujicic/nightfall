package deimophobe.nightfall.damage;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 6/05/17.
 */
public abstract class GameDamage<A extends GameEntity, R extends GameEntity> implements CancellableFinalGameDamage<A,R> {
	public static final double INSTA_KILL_DMG = 1000000;
	private static final int DEFAULT_NO_DMG_TICKS = 10;
	
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
	/** The item which was used to hit. If not applicable this value is null. */
	private ItemStack itemStack;
	/** The death message maker that will be used to generate the death message if this is the final blow. */
	private DeathMessageMaker deathMessageMaker;
	
	/** How much knockback to do. */
	protected Vector knockback;
	/** If set to true, the damage will no longer occur. Overrides force. */
	protected boolean cancelled;
	/** If set to true, the damage will not occur, but there will still be a damage tick. */
	protected boolean softCancelled;
	/** Number of invincibility ticks. */
	protected int noDamageTicks;
	/** Number of flame ticks. */
	protected int fireTicks = -1;
	/** If set to true, damage will be 'infinite'. */
	protected boolean instaKill;
	
	private final Projectile projectile;
	
	private final Set<DamageHandler<CancellableFinalGameDamage<A,R>>> preDamageHandlers = new HashSet<>();
	private final Set<DamageHandler<FinalGameDamage<A,R>>> postDamageHandlers = new HashSet<>();
	
	private static int idCount = 0;
	/** Currently only used for debugging */
	private final int id;
	
	
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
	
	protected GameDamage(A attacker, R receiver, GameDamageType type, double damage, Projectile projectile) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.phase = DamagePhase.PRE_FIRE;
		this.itemStack = getHeldItemOfDamager(attacker);
		this.deathMessageMaker = type.getDefaultDeathMessageMaker();
		
		this.mulitPartDamage = new MultiPartValue(damage);
		int resLevel = receiver.getPotionEffectLevel(PotionEffectType.DAMAGE_RESISTANCE);
		double res = Math.min(1, 1 - resLevel*0.2);
		mulitPartDamage.timesMult(res);
		
		this.cancelled = false;
		this.softCancelled = false;
		this.noDamageTicks = DEFAULT_NO_DMG_TICKS;
		this.instaKill = false;
		
		this.projectile = projectile;
		
		if (type == GameDamageType.MELEE) {
			if (itemStack != null) {
				int burnLevel = itemStack.getEnchantmentLevel(Enchantment.FIRE_ASPECT);
				if (burnLevel > 0) fireTicks = burnLevel * 4 * 20;
			}
			setKnockbackFromMelee();
		} else if (type == GameDamageType.RANGED) {
			setKnockbackFromArrow();
		} else {
			this.knockback = null;
		}
		
		this.id = idCount;
		idCount++;
		
		type.applyModifier(this);
	}
	
	public void setKnockbackFromMelee() {
		if (attacker == null) return;
		
		int knockbackLevel = 0;
		if (itemStack != null) knockbackLevel = itemStack.getEnchantmentLevel(Enchantment.KNOCKBACK);
		
		Vector offset = receiver.offsetFrom(attacker);
		offset.setY(0).normalize().multiply(0.5 + 0.35 * knockbackLevel);
		offset.setY(0.3 + 0.05 * knockbackLevel);
		offset.add(attacker.getVelocity().setY(0).multiply(0.5));
		
		knockback = offset;
		
		makeKnockbackFinite();
	}
	
	public void setKnockbackFromArrow() {
		if (!hasArrow()) return;
		
		Vector offset = projectile.getVelocity();
		
		int punchLevel = 0;
		float force = 1;
		if (projectile instanceof Arrow) {
			punchLevel = ((Arrow) projectile).getKnockbackStrength();
			force = ArrowMisc.getArrowForce((Arrow) projectile);
		}
		
		offset.setY(0).normalize().multiply(0.6 + 0.4 * punchLevel);
		offset.setY(0.35 + 0.05*punchLevel);
		offset.multiply(force);
		
		knockback = offset;
		
		makeKnockbackFinite();
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
	
	public void setNoDamageTicks(int ticks) { noDamageTicks = ticks; }
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
	
	/** Only used for the special case of handling mob deaths */
	@Deprecated
	protected void forceSoftCancel() {
		instaKill = false;
		cancelled = false;
		softCancelled = true;
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
	private void makeKnockbackFinite() {
		if (knockback == null) return;
		
		if (!NumberConversions.isFinite(knockback.getX())) knockback.setX(0);
		if (!NumberConversions.isFinite(knockback.getY())) knockback.setY(0);
		if (!NumberConversions.isFinite(knockback.getZ())) knockback.setZ(0);
	}
	
	public double getFinalDamage() {
		if (instaKill) return INSTA_KILL_DMG;
		if (softCancelled || cancelled) return 0;
		
		return mulitPartDamage.getValue();
	}
	
	public boolean willKill() {
		return (receiver.getHealth() - getFinalDamage() <= 0 || instaKill);
	}
	
	public boolean hasArrow() {return  projectile instanceof Arrow;}
	public Arrow getArrow() {
		if (projectile instanceof Arrow)
			return (Arrow) projectile;
		else
			throw new IllegalStateException("Tried to access arrow of gameDamage which has no arrow.");
	}
	
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	public void setDeathMessageMaker(DeathMessageMaker deathMessageMaker) {
		this.deathMessageMaker = deathMessageMaker;
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
	public boolean fire() {
		return fire(false);
	}
	public boolean fire(boolean force) {
		return onFire(force);
	}
	
	/** Fires a custom damage event */
	private boolean onFire(boolean force) {
		// Check that damage has not already been fired
		if (phase != DamagePhase.PRE_FIRE) throw new IllegalStateException("Already fired damage: " + this);
		
		// Check if damage is allowed to occur by game ticks
		if (!force && receiver.getEntity().getNoDamageTicks() != 0) return false;
		if (cancelled) return false;
		
		
		// Notify attacker and receiver, and let them set up their events
		phase = DamagePhase.NOTIFYING;
		notifyEntities();
		if (cancelled) return false;
		
		
		// Apply pre damage handlers, stop if necessary
		phase = DamagePhase.PRE_DAMAGE;
		for (DamageHandler<CancellableFinalGameDamage<A, R>> handler : Misc.asSortedList(preDamageHandlers)) {
			handler.consume(this);
			if (cancelled) return false;
		}
		
		
		// Do the damage
		phase = DamagePhase.DAMAGING;
		double doDamageAmt = getFinalDamage();
		if (doDamageAmt <= 0) {
			if (doDamageAmt < 0) Bukkit.getLogger().warning("Game Damage " + id + " has less than zero damage!");
			
			doDamageAmt = 100;
			softCancel();
		}
		
		// Notify gamePlayer to save damage. This happens before damage so that death messages from the damage have the right info.
		long time = System.currentTimeMillis();
		if (receiver instanceof GamePlayer) {
			LastMainDamage lastMainDamage = new LastMainDamage(attacker, type, itemStack, time);
			((GamePlayer) receiver).saveDamageInfo(deathMessageMaker, lastMainDamage);
		}
		
		LivingEntity receiverEntity = receiver.getEntity();
		receiverEntity.setNoDamageTicks(0);
		DamageUtil.processingDamage = this;
		receiverEntity.damage(doDamageAmt);
		DamageUtil.processingDamage = null;
		receiverEntity.setNoDamageTicks(noDamageTicks);
		
		// Apply knockback
		if (knockback != null) {
			double kbResist = receiverEntity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
			if (0 <= kbResist && kbResist < 1) {
				knockback.multiply(1 - kbResist);
				receiver.setVelocity(knockback);
			}
		}
		
		// Apply fire ticks
		if (fireTicks != -1) {
			new BukkitRunnable() {
				@Override public void run() { receiverEntity.setFireTicks(fireTicks); }
			}.runTask(NightfallPlugin.getPlugin());
		}
		
		
		// Apply post damage handlers
		phase = DamagePhase.POST_DAMAGE;
		Misc.asSortedList(postDamageHandlers).forEach(h -> h.consume(this));
		
		// Debug
		if (attacker instanceof GamePlayer) ((GamePlayer) attacker).debugObject(this);
		if (receiver instanceof GamePlayer) ((GamePlayer) receiver).debugObject(this);
		
		return true;
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
		
		return "GameDamage ID" + id + " from " + attackerName + ChatColor.RESET + " to " + receiver.getName() + ChatColor.RESET + " of type: " + type + ".\n"
				+ "  DAMAGES - " + mulitPartDamage.toString() + "\n"
				+ (knockback != null ? "  Knockback: " + df.format(knockback.length()) + "\n" : "")
				+ "  NoDmgTicks: " + noDamageTicks + "; FireTicks: " + fireTicks + ".\n"
				+ "  Pre Handlers: " + preDamageHandlers.size() + "; " + "Post Handlers: " + postDamageHandlers.size() + "\n"
				+ (extraString.length() > 0 ? "  EXTRA - " + extraString.toString() + "\n" : "");
	}
}
