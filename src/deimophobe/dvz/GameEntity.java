package deimophobe.dvz;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class GameEntity {
	
	private final LivingEntity entity;
	
	protected GameEntity(LivingEntity entity) {
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
		entity.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
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
	
	public GameEntity getLastDamager() {
		return lastDamager;
	}
	
	public DamageType getLastDamageType() {
		return lastDamageType;
	}
	
	public void customDamage(GameEntity damager, DamageType type, double damage) {
		lastDamager = damager;
		lastDamageType = type;
		
		if (!type.isCustom())
			Bukkit.getLogger().warning("Forcing custom damage that is not of custom type?!");
		
		entity.damage(damage);
	}
	
	public void registerNonCustomDamage(GameEntity damager, DamageType type) {
		lastDamager = damager;
		lastDamageType = type;
		
		if (type.isCustom())
			Bukkit.getLogger().warning("Registering damage that is of custom type?!");
	}
	
	
	// ------ POTION EFFECTS ------
	// TODO experiment
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean ambient, boolean force) {
		entity.addPotionEffect(new PotionEffect(type, duration, amplifier-1, ambient), force);
	}
	
	public void clearEffects() {
		for (PotionEffect effect : entity.getActivePotionEffects()){
			entity.removePotionEffect(effect.getType());
		}
	}
	
	
	public abstract double onHit(GameEntity entity, DamageType type, double damage);
	public abstract double onGotHit(GameEntity entity, DamageType type, double damage);
	
}
