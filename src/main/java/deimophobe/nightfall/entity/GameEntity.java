package deimophobe.nightfall.entity;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.damage.DamageOverTimeType;
import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.GameDamageType;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
	
	default double distanceTo(Location center) {
		return getLocation().distance(center);
	}
	
	default double horizantalDistance(Location location) {
		Location entityLoc = getEntity().getLocation();
		double xDiff = location.getX() - entityLoc.getX();
		double zDiff = location.getZ() - entityLoc.getZ();
		return Math.sqrt(xDiff*xDiff + zDiff*zDiff);
	}
	
	default Vector offsetFrom(GameEntity entity) {
		return getLocation().subtract(entity.getLocation()).toVector();
	}
	
	default Vector offsetFrom(Location location) {
		return getLocation().subtract(location).toVector();
	}
	
	default World getWorld() {
		return getEntity().getWorld();
	}
	
	// ------ VELOCITY ------
	default Vector getVelocity() {
		return getEntity().getVelocity();
	}
	
	default void setVelocity(double vx, double vy, double vz) {
		setVelocity(new Vector(vx, vy, vz));
	}
	
	default void setVelocity(Vector velocity) {
		getEntity().setVelocity(velocity);
	}
	
	default void addVelocity(Vector velocity) {
		setVelocity(getVelocity().add(velocity));
	}
	
	default void leap(double horizontal, double vertical) {
		double yaw = getEntity().getLocation().getYaw();
		double radYaw = yaw*Math.PI/180;
		setVelocity(-horizontal * Math.sin(radYaw), vertical, horizontal * Math.cos(radYaw));
	}
	
	// ------ HEALTH ------
	default double getHealth() {
		return getEntity().getHealth();
	}
	
	default double getMaxHealth() {
		return getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
	}
	
	default void heal(double amt) {
		LivingEntity entity = getEntity();
		if (entity.isDead()) return;
		
		double newHealth = amt + getEntity().getHealth();
		double maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		if (newHealth < maxHealth) {
			entity.setHealth(newHealth);
		} else {
			entity.setHealth(maxHealth);
		}
		//getEntity().getDamage(0);
	}
	
	default void healMax() {
		LivingEntity entity = getEntity();
		if (entity.isDead()) return;
		
		double maxHealth = getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		getEntity().setHealth(maxHealth);
		
		/*
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
		*/
		//getEntity().getDamage(0);
	}
	
	default void delayedHealMax() {
		LivingEntity entity = getEntity();
		if (entity.isDead()) return;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				healMax();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), 20);
	}
	
	
	// ------ DAMAGE ------
	default boolean doDamage(GameEntity attacker, GameDamageType type, double damage) {
		return doDamage(attacker, type, damage, false, false);
	}
	
	default boolean doDamage(GameEntity attacker, GameDamageType type, double damage, boolean force) {
		return doDamage(attacker, type, damage, force, false);
	}
	
	default boolean doDamage(GameEntity attacker, GameDamageType type, double damage, boolean force, boolean instaKill) {
		GameDamage gameDamage = createDamage(attacker, type, damage);
		if (instaKill)
			gameDamage.instaKill();
		return gameDamage.fire(force);
	}
	
	default GameDamage createDamage(GameEntity attacker, GameDamageType type, double damage) {
		return GameDamage.createDamage(attacker, this, type, damage);
	}
	
	default String getDeathMessageName() {
		return getDisplayName();
	}
	
	
	void doDamageOverTimeTick(DamageOverTimeType type);
	
	boolean canDamageOverTimeTick(DamageOverTimeType type, long requiredDelay);
	
	// ------ POTION EFFECTS ------
	default boolean givePotionEffect(PotionEffectType type, int duration, int amplifier, boolean showAbove, boolean colourBlue, boolean force) {
		if (amplifier == 0) return false;
		
		if (!force) {
			PotionEffect effect = getEntity().getPotionEffect(type);
			if (effect != null && effect.getDuration() <= duration)
				force = true;
		}
		
		return getEntity().addPotionEffect(new PotionEffect(type, duration, amplifier-1, colourBlue, showAbove), force);
	}
	
	int MAX_POTION_LENGTH = 10*60*60*20;
	default void givePermanentPotionEffect(PotionEffectType type, int amplifier) {
		givePermanentPotionEffect(type, amplifier, true);
	}
	
	default void givePermanentPotionEffect(PotionEffectType type, int amplifier, boolean isBlue) {
		givePotionEffect(type, MAX_POTION_LENGTH, amplifier, true, isBlue, true);
	}
	
	default void clearEffects() {
		for (PotionEffect effect : getEntity().getActivePotionEffects()){
			removePotionEffect(effect.getType());
		}
	}
	
	default boolean hasPotionEffect(PotionEffectType type) {
		return getEntity().hasPotionEffect(type);
	}
	
	default int getPotionEffectLevel(PotionEffectType type) {
		PotionEffect effect = getEntity().getPotionEffect(type);
		if (effect == null) return 0;
		return effect.getAmplifier() + 1;
	}
	
	default int getPotionEffectDuration(PotionEffectType type) {
		PotionEffect effect = getEntity().getPotionEffect(type);
		if (effect == null) return 0;
		return effect.getDuration();
	}
	
	default void removePotionEffect(PotionEffectType type) {
		getEntity().removePotionEffect(type);
	}
	
	
	
	// ----- MISC -----
	
	default boolean isUnderwater() {
		Block lowerBlock = getEntity().getLocation().getBlock();
		Block upperBlock = lowerBlock.getRelative(BlockFace.UP);
		return (lowerBlock.isLiquid() || upperBlock.isLiquid());
	}
	
	default Disguise getDisguise() {
		return DisguiseAPI.getDisguise(getEntity());
	}
}
