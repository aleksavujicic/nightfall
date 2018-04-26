package deimophobe.nightfall.damage;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.damage.death.DeathMessageMaker;
import deimophobe.nightfall.damage.death.LastMainDamage;
import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.util.ArrowMisc;
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

/**
 * Created by Deimophobe on 6/05/17.
 */
public abstract class GameDamage<A extends GameEntity, R extends GameEntity> {
	public static final double INSTA_KILL_DMG = 1000000;
	private static final int DEFAULT_NO_DMG_TICKS = 10;
	
	/** The type of damage. */
	protected final GameDamageType type;
	/** The GameEntity which initiated the damage. */
	protected final A attacker;
	/** The GameEntity which receives the damage. */
	protected final R receiver;
	/** How much damage to do. */
	private MultiPartValue multiPartDamage;
	
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
	
	private final Set<DamageHandler<PreDamagePriority>> preDamageHandlers = new HashSet<>();
	private final Set<DamageHandler<PostDamagePriority>> postDamageHandlers = new HashSet<>();
	
	private static int idCount = 0;
	/** Currently only used for debugging */
	private final int id;
	
	
	// ------ CONSTRUCTORS -------
	public GameDamage(A attacker, R receiver, GameDamageType type, double damage, Projectile projectile) {
		this.type = type;
		this.attacker = attacker;
		this.receiver = receiver;
		
		this.phase = DamagePhase.PRE_FIRE;
		this.itemStack = getHeldItemOfDamager(attacker);
		this.deathMessageMaker = type.getDefaultDeathMessageMaker();
		
		this.multiPartDamage = new MultiPartValue(damage);
		
		int resLevel = receiver.getPotionEffectLevel(PotionEffectType.DAMAGE_RESISTANCE);
		double res = Misc.boundValue( 1 - resLevel*0.2, 0, 1);
		multiPartDamage.timesMult(res);
		
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
		
		type.applyModifier(this);
		if (type.isArrow() && attacker instanceof GamePlayer && receiver instanceof GamePlayer) {
			addPostDamageHandler(
					() -> ((GamePlayer) attacker).playSound("entity.arrow.hit_player", 0.8f, 0.5f, false)
			);
		}
		if (projectile instanceof Arrow) {
			ArrowMisc.applyDamageModifiers((Arrow) projectile, this);
		}
		
		this.id = idCount;
		idCount++;
	}
	
	public void setKnockbackFromMelee() {
		if (attacker == null) return;
		
		int knockbackLevel = 0;
		if (itemStack != null) knockbackLevel = itemStack.getEnchantmentLevel(Enchantment.KNOCKBACK);
		
		Vector offset = receiver.offsetFrom(attacker);
		offset.setY(0).normalize().multiply(0.5 + 0.35 * knockbackLevel);
		offset.setY(0.3 + 0.05 * knockbackLevel);
		
		Vector attackerVel = attacker.getVelocity().setY(0);
		double length = attackerVel.length();
		attackerVel.multiply(0.5f/(length + 1));
		offset.add(attackerVel);
		
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
	
	public MultiPartValue getMultiPartDamage() { return multiPartDamage; }
	
	public void setNoDamageTicks(int ticks) {
		noDamageTicks = ticks;
	}
	public void reduceNoDamageTicks(int ticks) {
		noDamageTicks = Math.min(noDamageTicks, ticks);
	}
	public void setFireTicks(int ticks) {
		fireTicks = ticks;
	}
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
	public void multiplyKnockback(double horizontal, double vertical) {
		initialiseKnockbackIfNull();
		knockback.setX(horizontal * knockback.getX());
		knockback.setY(vertical * knockback.getY());
		knockback.setZ(horizontal * knockback.getZ());
		knockback.checkFinite();
	}
	public Vector getKnockback() {
		return knockback;
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
		
		return multiPartDamage.getValue();
	}
	
	public boolean willKill() {
		return (receiver.getHealth() - getFinalDamage() <= 0.000001 || instaKill);
	}
	
	public Projectile getProjectile() { return projectile; }
	public boolean hasArrow() { return (projectile instanceof Arrow); }
	public Arrow getArrow() {
		if (projectile instanceof Arrow) {
			return (Arrow) projectile;
		} else {
			throw new IllegalStateException("Tried to access arrow of GameDamage which has no arrow.");
		}
	}
	
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
	public void setDeathMessageMaker(DeathMessageMaker deathMessageMaker) {
		this.deathMessageMaker = deathMessageMaker;
	}

	
	/**
	 * Adds a {@link Runnable} that will run just before the damage is executed. Will not be run if damage is cancelled earlier.
	 * The main use case for these handlers is to correctly cancel damage in certain scenarios. They should ideally only use the
	 * following methods:
	 * <ul>
	 *   <li>{@link #getAttacker()}</li>
	 *   <li>{@link #getReceiver()}</li>
	 *   <li>{@link #getType()}</li>
	 *   <li>{@link #getFinalDamage()}</li>
	 *   <li>{@link #willKill()}</li>
	 *   <li>{@link #cancel()}</li>
	 *   <li>{@link #softCancel()}</li>
	 * </ul>
	 * Any code that needs to guarantee the success and damage of this GameDamage should use {@link #addPostDamageHandler(Runnable)} instead.
	 *
	 * @param handler The {@link Runnable} code to be run.
	 *
	 * @see deimophobe.nightfall.dwarf.kit.healing.SafetyJuice
	 * @see deimophobe.nightfall.dwarf.kit.accessory.Resurrection
	 */
	public void addPreDamageHandler(Runnable handler) {
		addPreDamageHandler(PreDamagePriority.DEFAULT, handler);
	}
	/**
	 * Adds a {@link Runnable} that will run just before the damage is executed. Will not be run if damage is cancelled earlier.
	 * The main use case for these handlers is to correctly cancel damage in certain scenarios. They should ideally only use the
	 * following methods:
	 * <ul>
	 *   <li>{@link #getAttacker()}</li>
	 *   <li>{@link #getReceiver()}</li>
	 *   <li>{@link #getType()}</li>
	 *   <li>{@link #getFinalDamage()}</li>
	 *   <li>{@link #willKill()}</li>
	 *   <li>{@link #cancel()}</li>
	 *   <li>{@link #softCancel()}</li>
	 * </ul>
	 * Any code that needs to guarantee the success and damage of this GameDamage should use {@link #addPostDamageHandler(PostDamagePriority, Runnable)} instead.
	 *
	 * @param priority Determines when this handler will be run in comparison to other handlers.
	 * @param handler The {@link Runnable} code to be run.
	 *
	 * @see deimophobe.nightfall.dwarf.kit.healing.SafetyJuice
	 * @see deimophobe.nightfall.dwarf.kit.accessory.Resurrection
	 */
	public void addPreDamageHandler(PreDamagePriority priority, Runnable handler) {
		preDamageHandlers.add(new DamageHandler<>(priority, handler));
	}
	
	/**
	 * Adds a {@link Runnable} that will run after the damage is successfully completed. Will not be run if damage is not successful.
	 * As the damage is completed, changing damage values is mostly meaningless and highly discouraged. Ideally only the following methods
	 * should be run:
	 * <ul>
	 *   <li>{@link #getAttacker()}</li>
	 *   <li>{@link #getReceiver()}</li>
	 *   <li>{@link #getType()}</li>
	 *   <li>{@link #getFinalDamage()}</li>
	 *   <li>{@link #willKill()}</li>
	 * </ul>
	 * Any code that needs to cancel this GameDamage should use {@link #addPreDamageHandler(Runnable)} instead.
	 *
	 * @param handler The {@link Runnable} code to be run.
	 */
	public void addPostDamageHandler(Runnable handler) {
		addPostDamageHandler(PostDamagePriority.DEFAULT, handler);
	}
	/**
	 * Adds a {@link Runnable} that will run after the damage is successfully completed. Will not be run if damage is not successful.
	 * As the damage is completed, changing damage values is mostly meaningless and highly discouraged. Ideally only the following methods
	 * should be run:
	 * <ul>
	 *   <li>{@link #getAttacker()}</li>
	 *   <li>{@link #getReceiver()}</li>
	 *   <li>{@link #getType()}</li>
	 *   <li>{@link #getFinalDamage()}</li>
	 *   <li>{@link #willKill()}</li>
	 * </ul>
	 * Any code that needs to cancel this GameDamage should use {@link #addPreDamageHandler(Runnable)} instead.
	 *
	 * @param priority Determines when this handler will be run in comparison to other handlers.
	 * @param handler The {@link Runnable} code to be run.
	 */
	public void addPostDamageHandler(PostDamagePriority priority, Runnable handler) {
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
		for (DamageHandler<?> handler : Misc.asSortedList(preDamageHandlers)) {
			handler.run();
			if (cancelled) return false;
		}
		
		
		// Do the damage
		phase = DamagePhase.DAMAGING;
		double doDamageAmt = getFinalDamage();
		if (doDamageAmt <= 0) {
			if (doDamageAmt < 0) NightfallPlugin.logger().warning("Game Damage " + id + " has less than zero damage!");
			
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
				@Override public void run() {
					int entityFireTicks = receiverEntity.getFireTicks();
					receiverEntity.setFireTicks(Math.max(fireTicks, entityFireTicks));
				}
			}.runTask(NightfallPlugin.getPlugin());
		}
		
		
		// Apply post damage handlers
		phase = DamagePhase.POST_DAMAGE;
		Misc.asSortedList(postDamageHandlers).forEach(DamageHandler::run);
		
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
				+ "  DAMAGES - " + multiPartDamage.toString() + "\n"
				+ (knockback != null ? "  Knockback: " + df.format(knockback.length()) + "\n" : "")
				+ "  NoDmgTicks: " + noDamageTicks + "; FireTicks: " + fireTicks + ".\n"
				+ "  Pre Handlers: " + preDamageHandlers.size() + "; " + "Post Handlers: " + postDamageHandlers.size() + "\n"
				+ (extraString.length() > 0 ? "  EXTRA - " + extraString.toString() + "\n" : "");
	}
}
