package deimophobe.nightfall.entity;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.damage.DamageModifier;
import deimophobe.nightfall.damage.type.CustomDamageType;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by Deimophobe on 24/01/17.
 */
public interface GameEntity<E extends LivingEntity> {
	E getEntity();
	
	default String getName() {
		return getEntity().getName();
	}
	default String getDisplayName() {
		return getEntity().getCustomName();
	}
	
	default UUID getUniqueId() {
		return getEntity().getUniqueId();
	}
	
	default boolean isDead() {
		return getEntity().isDead();
	}
	
	// ------ LOCATION ------
	default Location getLocation() {
		return getEntity().getLocation();
	}
	
	default Location getEyeLocation() {
		return getEntity().getEyeLocation();
	}
	
	default void teleportTo(Location loc) {
		teleportTo(loc, false);
	}
	
	default void teleportTo(Location loc, boolean keepEyeDirection) {
		if (keepEyeDirection)
			loc.setDirection(getEntity().getLocation().getDirection());
		getEntity().teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
	}
	
	default double distanceTo(GameEntity entity) {
		return getLocation().distance(entity.getLocation());
	}
	
	
	// ------ VELOCITY ------
	default Vector getVelocity() {
		return getEntity().getVelocity();
	}
	
	default void setVelocity(Vector vel) {
		getEntity().setVelocity(vel);
	}
	
	default void setVelocity(double vx, double vy, double vz) {
		setVelocity(new Vector(vx, vy, vz));
	}
	
	// ------ HEALTH ------
	default double getHealth() {
		return getEntity().getHealth();
	}
	
	default void heal(double amt) {
		double newHealth = amt + getEntity().getHealth();
		double maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (newHealth < maxHealth) {
			getEntity().setHealth(newHealth);
		} else {
			getEntity().setHealth(maxHealth);
		}
		//getEntity().damage(0);
	}
	
	default void healMax() {
		double maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		getEntity().setHealth(maxHealth);
		
		if (getEntity() instanceof Player) {
			Player p = (Player) getEntity();
			
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
		//getEntity().damage(0);
	}
	
	default void delayedHealMax() {
		new BukkitRunnable() {
			@Override
			public void run() {
				healMax();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 20);
	}
	
	
	// ------ DAMAGE ------
	default void damage(GameEntity attacker, CustomDamageType type, double damage) {
		damage(attacker, type, damage, new DamageModifier());
	}
	
	default void damage(GameEntity attacker, CustomDamageType type, double damage, DamageModifier modifier) {
		DamageManager.getManager().customDamage(attacker, this, type, damage, modifier);
	}
	
	
	// ------ POTION EFFECTS ------
	default void givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		if (amplifier == 0) return;
		
		if (!force) {
			PotionEffect effect = getEntity().getPotionEffect(type);
			if (effect != null && effect.getDuration() <= duration)
				force = true;
		}
		
		getEntity().addPotionEffect(new PotionEffect(type, duration, amplifier-1, colourBlue, showAbove), force);
	}
	
	int MAX_POTION_LENGTH = 10*60*60*20;
	default void givePermanentPotionEffect(PotionEffectType type, int amplifier) {
		givePotionEffect(type, MAX_POTION_LENGTH, amplifier, true, true, true);
	}
	
	default void clearEffects() {
		for (PotionEffect effect : getEntity().getActivePotionEffects()){
			removePotionEffect(effect.getType());
		}
	}
	
	default boolean hasPotionEffect(PotionEffectType type) {
		return getEntity().hasPotionEffect(type);
	}
	
	default void removePotionEffect(PotionEffectType type) {
		getEntity().removePotionEffect(type);
	}
}
