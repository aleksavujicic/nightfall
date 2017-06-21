package deimophobe.dvz;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.inventivetalent.glow.GlowAPI;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 24/01/17.
 */
public abstract class GameEntity<E extends LivingEntity> {
	protected E entity;
	
	public E getEntity() {
		return entity;
	}
	
	protected GameEntity(E entity) {
		this.entity = entity;
	}
	protected void resetEntity(E entity) {
		this.entity = entity;
	}
	
	
	public String getName() {
		return entity.getName();
	}
	public String getDisplayName() {
		return entity.getCustomName();
	}
	
	public UUID getUniqueId() {
		return entity.getUniqueId();
	}
	
	public boolean isDead() {
		return entity.isDead();
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
	
	public void setVelocity(double vx, double vy, double vz) {
		setVelocity(new Vector(vx, vy, vz));
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
		double maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		entity.setHealth(maxHealth);
		
		if (entity instanceof Player) {
			Player p = (Player) entity;
			
			ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
			PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.UPDATE_HEALTH);
			packet.getFloat().write(0, (float) maxHealth);
			packet.getFloat().write(1, 5f);
			packet.getIntegers().write(0, 20);
			
			try {
				protocolManager.sendServerPacket(p, packet);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
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
	private long lastDamageTime = 0;
	private GameEntity lastDamager;
	private DamageType lastDamageType;
	private String lastItemName;
	
	private static final long MAX_PREV_DMG_STORE_TIME = 200;
	
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
		registerDamage(damager, type);
		
		if (force)
			entity.setNoDamageTicks(0);
		
		entity.damage(damage);
	}
	
	public void registerDamage(GameEntity damager, DamageType type) {
		lastDamageType = type;
		if (type.doesOverwriteAttacker() || lastDamager instanceof AIEntity) {
			lastDamager = damager;
			lastItemName = getHeldItemOfDamager(damager);
			lastDamageTime = entity.getWorld().getFullTime();
		} else if (entity.getWorld().getFullTime() - lastDamageTime > MAX_PREV_DMG_STORE_TIME) {
			lastDamager = null;
			lastItemName = null;
		}
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
	public void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		if (amplifier == 0) return;
		
		if (!force) {
			PotionEffect effect = entity.getPotionEffect(type);
			if (effect != null && effect.getDuration() <= duration)
				force = true;
		}
		
		entity.addPotionEffect(new PotionEffect(type, duration, amplifier-1, colourBlue, showAbove), force);
	}
	
	private static final int MAX_POTION_LENGTH = 10*60*60*20;
	public void givePermanentPotionEffect(PotionEffectType type, int amplifier) {
		givePotionEffect(type, MAX_POTION_LENGTH, amplifier, true, true, true);
	}
	
	public void setGlowing(int duration, GlowAPI.Color color) {
		GlowAPI.setGlowing(entity, color, Bukkit.getOnlinePlayers());
		new BukkitRunnable() {
			@Override
			public void run() {
				GlowAPI.setGlowing(entity, false, Bukkit.getOnlinePlayers());
			}
		}.runTaskLater(Game.getGame().getPlugin(), duration);
	}
	
	public void setGlowing(GlowAPI.Color color) {
		GlowAPI.setGlowing(entity, color, Bukkit.getOnlinePlayers());
	}
	
	public void clearEffects() {
		for (PotionEffect effect : entity.getActivePotionEffects()){
			entity.removePotionEffect(effect.getType());
		}
	}
	
	public void removePotionEffect(PotionEffectType type) {
		entity.removePotionEffect(type);
	}
	
	public abstract double onHit(GameEntity entity, DamageType type, double damage);
	public abstract double onGotHit(GameEntity entity, DamageType type, double damage);
	
}
