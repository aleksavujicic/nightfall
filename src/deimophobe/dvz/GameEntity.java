package deimophobe.dvz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Calendar;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class GameEntity {
	
	private LivingEntity entity;
	
	protected GameEntity(LivingEntity entity) {
		this.entity = entity;
	}
	
	protected void resetEntity(LivingEntity entity) {
		this.entity = entity;
	}
	
	
	public String getName() {
		return entity.getName();
	}
	public String getDisplayName() {
		return entity.getCustomName();
	}
	
	// ------ LOCATION ------
	public Location getLocation() {
		return entity.getLocation();
	}
	
	public Location getEyeLocation() {
		return entity.getEyeLocation();
	}
	
	public void teleportTo(Location loc) {
		teleportTo(loc, false);
	}
	
	public void teleportTo(Location loc, boolean keepEyeDirection) {
		if (keepEyeDirection)
			loc.setDirection(entity.getLocation().getDirection());
		entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
	}
	
	public double distanceTo(GameEntity entity) {
		return getLocation().distance(entity.getLocation());
	}
	
	
	// ------ VELOCITY ------
	public Vector getVelocity() {
		return entity.getVelocity();
	}
	
	public void setVelocity(Vector vel) {
		entity.setVelocity(vel);
	}
	
	
	// ------ HEALTH ------
	public double getHealth() {
		return entity.getHealth();
	}
	
	public void heal(double amt) {
		double newHealth = amt + entity.getHealth();
		double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (newHealth < maxHealth) {
			entity.setHealth(newHealth);
		} else {
			entity.setHealth(maxHealth);
		}
		//entity.damage(0);
	}
	
	public void healMax() {
		entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		//entity.damage(0);
	}
	
	public void delayedHealMax() {
		new BukkitRunnable() {
			@Override
			public void run() {
				healMax();
			}
		}.runTaskLater(Game.getGame().getPlugin(), 20);
	}
	
	
	// ------ DAMAGE ------
	private GameEntity lastDamager;
	private DamageType lastDamageType;
	private String lastItemName;
	
	public GameEntity getLastDamager() {
		return lastDamager;
	}
	public DamageType getLastDamageType() {
		return lastDamageType;
	}
	public String getLastItemName() {
		return lastItemName;
	}
	
	public void customDamage(GameEntity damager, DamageType type, double damage) {
		customDamage(damager, type, damage, false);
	}
	
	public void customDamage(GameEntity damager, DamageType type, double damage, boolean force) {
		lastDamager = damager;
		lastDamageType = type;
		lastItemName = getHeldItemOfDamager(damager);
		
		if (!type.isCustom())
			Bukkit.getLogger().warning("Forcing custom damage that is not of custom type?!");
		
		if (force)
			entity.setNoDamageTicks(0);
		
		entity.damage(damage);
	}
	
	public void registerNonCustomDamage(GameEntity damager, DamageType type) {
		lastDamager = damager;
		lastDamageType = type;
		lastItemName = getHeldItemOfDamager(damager);
		
		if (type.isCustom())
			Bukkit.getLogger().warning("Registering damage that is of custom type?!");
	}
	
	private static String getHeldItemOfDamager(GameEntity damager) {
		if (!(damager instanceof GamePlayer)) return null;
		
		GamePlayer gp = ((GamePlayer) damager);
		ItemStack item = gp.getHeldItem();
		if (item == null) return null;
		
		ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;
		
		return meta.getDisplayName();
	}
	
	public void kill() {
		customDamage(null, DamageType.KILL, 10000);
	}
	
	
	// ------ POTION EFFECTS ------
	// TODO experiment
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		entity.addPotionEffect(new PotionEffect(type, duration, amplifier-1, colourBlue, showAbove), force);
	}
	
	public void clearEffects() {
		for (PotionEffect effect : entity.getActivePotionEffects()){
			entity.removePotionEffect(effect.getType());
		}
	}
	
	public abstract double onHit(GameEntity entity, DamageType type, double damage);
	public abstract double onGotHit(GameEntity entity, DamageType type, double damage);
	
}
