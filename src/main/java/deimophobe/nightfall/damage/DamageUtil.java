package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.util.ArrowMisc;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 25/08/17.
 */
public class DamageUtil {
	
	protected static GameDamage<?,?> processingDamage = null;
	public static void fireDamage(GameDamage<?,?> damage, boolean force) {
		damage.onFire(force);
	}
	
	public static void processDamageEvent(EntityDamageEvent event) {
		// If custom cause, then it is already being processed.
		if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
			if (processingDamage != null && processingDamage.softCancelled) event.setDamage(0);
			Bukkit.getLogger().info("CUSTOM: " + processingDamage);
			return;
		}
		// Don't do anything for this event. GameDamage will fire its own event.
		event.setCancelled(true);
		
		GameDamage damage = createDamageFromEvent(event);
		if (damage == null) {
			event.setCancelled(true);
			return;
		}
		
		damage.fire();
	}
	
	private static GameDamage<?,?> createDamageFromEvent(EntityDamageEvent event) {
		GameEntity receiver = Game.getGame().getGameEntity(event.getEntity());
		
		GameDamage<?,?> gameDamage;
		GameDamageType type;
		
		switch (event.getCause()) {
			
			case ENTITY_ATTACK: {
				Entity entityAttacker = ((EntityDamageByEntityEvent) event).getDamager();
				GameEntity attacker = Game.getGame().getGameEntity(entityAttacker);
				
				if (attacker == null && entityAttacker != null && entityAttacker instanceof Damageable) {
					((LivingEntity) entityAttacker).damage(100000);
					return null;
				}
				
				type = GameDamageType.MELEE;
				gameDamage = GameDamage.createDamage(attacker, receiver, type, event.getDamage());
				break;
			}
			
			case PROJECTILE: {
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				double damage;
				if (proj instanceof Arrow)
					damage = ArrowMisc.getArrowDamage((Arrow) proj);
				else
					damage = event.getDamage();
				GameEntity attacker = Game.getGame().getGameEntity((Entity) proj.getShooter());
				
				type = GameDamageType.RANGED;
				gameDamage = GameDamage.createDamage(attacker, receiver, type, damage, proj);
				break;
			}
			
			default: {
				type = GameDamageType.getTypeFromEventCause(event.getCause());
				gameDamage = GameDamage.createDamage(null, receiver, type, event.getDamage());
				break;
			}
		}
		
		type.applyModifier(gameDamage);
		return gameDamage;
	}
}
