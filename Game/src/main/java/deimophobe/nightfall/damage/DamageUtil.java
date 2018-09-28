package deimophobe.nightfall.damage;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.monster.ai.AIEntity;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 25/08/17.
 */
public class DamageUtil {
	
	protected static GameDamage<?,?> processingDamage = null;
	
	public static void processDamageEvent(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
			// If custom cause, then it is already being processed.
			
			// Only way I know to soft cancel
			if (processingDamage != null && processingDamage.softCancelled) event.setDamage(0);
			// Ignore resistance effects
			if (event.isApplicable(EntityDamageEvent.DamageModifier.RESISTANCE)) event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
		} else {
			// Don't do anything for this event. GameDamage will fire its own event.
			event.setCancelled(true);
			
			try {
				GameDamage damage = createDamageFromEvent(event);
				if (damage != null) damage.fire();
			} catch (UnknownDamageCauseException e) {
				NightfallPlugin.logger().severe(e.getMessage());
			}
		}
	}
	
	private static GameDamage<?,?> createDamageFromEvent(EntityDamageEvent event) throws UnknownDamageCauseException {
		GameEntity receiver = Game.getGame().getGameEntity(event.getEntity());
		switch (event.getCause()) {
			
			case ENTITY_ATTACK: {
				Entity entityAttacker = ((EntityDamageByEntityEvent) event).getDamager();
				GameEntity attacker = Game.getGame().getGameEntity(entityAttacker);
				
				if (attacker == null && entityAttacker != null && entityAttacker instanceof Damageable) {
					((LivingEntity) entityAttacker).damage(100000);
					return null;
				}
				
				double damage;
				if (entityAttacker instanceof LivingEntity) {
					damage = ((LivingEntity) entityAttacker).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
				} else {
					damage = event.getDamage();
				}
				boolean crit = (entityAttacker != null && !entityAttacker.isOnGround() && !(attacker instanceof AIEntity<?>));
				if (crit) damage *= 1.25;
				
				GameDamage<?,?> gameDamage = receiver.createDamage(attacker, GameDamageType.MELEE, damage);
				
				
				if (crit) {
					gameDamage.multiplyKnockback(1.2);
					gameDamage.addPostDamageHandler(() -> {
						receiver.getWorld().spawnParticle(Particle.CRIT, receiver.getEyeLocation().subtract(0, 0.2, 0), 3, 0.25, 0.25, 0.25, 0.1);
					});
				}
				
				return gameDamage;
			}
			
			case PROJECTILE: {
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				GameEntity attacker = Game.getGame().getGameEntity((Entity) proj.getShooter());
				double damage;
				if (proj instanceof Arrow) {
					damage = ArrowMisc.getArrowDamage((Arrow) proj);
				} else {
					damage = event.getDamage();
				}
				
				return receiver.createDamage(attacker, GameDamageType.RANGED, damage, proj);
			}
			
			default: {
				GameDamageType type = GameDamageType.getTypeFromEventCause(event.getCause());
				return receiver.createDamage(null, type, event.getDamage());
			}
		}
	}
}
