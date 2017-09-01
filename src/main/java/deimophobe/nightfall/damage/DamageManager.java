package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 25/08/17.
 */
public class DamageManager {
	
	public static DamageManager getManager() { return Game.getGame().getDamageManager(); }
	
	public DamageManager() {}
	
	public void customDamage(GameEntity attacker, GameEntity receiver, CustomDamageType type, double damage) {
		customDamage(attacker, receiver, type, damage, new DamageModifier());
	}
	
	public void customDamage(GameEntity attacker, GameEntity receiver, CustomDamageType type, double damage, DamageModifier modifier) {
		// Kinda a hack but eh im sick of this
		EntityDamageEvent event = new EntityDamageEvent(receiver.getEntity(), EntityDamageEvent.DamageCause.CUSTOM, damage);
		GameDamage gameDamage = GameDamage.createDamage(event, attacker, receiver, type, damage);
		modifier.applyToDamage(gameDamage);
		gameDamage.fire();
		
		if (!event.isCancelled())
			receiver.getEntity().damage(event.getDamage());
	}
			
	
	public void processDamageEvent(EntityDamageEvent event) {
		GameEntity damagee = Game.getGame().getGameEntity(event.getEntity());
		NaturalDamageType type;
		
		switch (event.getCause()) {
			// Already processed
			case CUSTOM:
				return;
				
			default:
				throw new IllegalArgumentException("Cannot create GameDamage with event cause " + event.getCause());
			
			case ENTITY_ATTACK: {
				GameEntity damager = Game.getGame().getGameEntity(((EntityDamageByEntityEvent) event).getDamager());
				
				if ((damager instanceof MonsterPlayer && damagee instanceof AIEntity) ||
						(damagee instanceof MonsterPlayer && damager instanceof AIEntity)) {
					event.setCancelled(true);
					return;
				} // TODO Move to AIEntity
				
				GameDamage.createDamage(event, damager, damagee, NaturalDamageType.MELEE, event.getDamage()).fire();
			}
			
			case PROJECTILE: {
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				GameEntity damager = Game.getGame().getGameEntity((Entity) proj.getShooter());
				GameDamage.createDamage(event, damager, damagee, NaturalDamageType.RANGED, event.getDamage(), proj).fire();
			}
			
				
			
			case CONTACT: type = NaturalDamageType.CONTACT; break;
			case DROWNING: type = NaturalDamageType.DROWNING; break;
			case HOT_FLOOR: type = NaturalDamageType.MAGMA_BLOCK; break;
			case FALL: type = NaturalDamageType.FALL; break;
			case LAVA: type = NaturalDamageType.LAVA; break;
			
			case FIRE:
			case FIRE_TICK:
				type = NaturalDamageType.FIRE;
				break;
			
			case POISON:
			case WITHER:
				type = NaturalDamageType.POISON;
				break;
			
			case VOID:
				type = NaturalDamageType.VOID;
				break;
		}
		GameDamage damage = GameDamage.createDamage(event, null, damagee, type, event.getDamage());
		type.applyDamage(damage);
		damage.fire();
	}
}
