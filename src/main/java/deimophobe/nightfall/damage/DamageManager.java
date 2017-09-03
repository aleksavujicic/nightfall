package deimophobe.nightfall.damage;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Function;

/**
 * Created by Deimophobe on 25/08/17.
 */
public class DamageManager {
	
	public static DamageManager getManager() { return Game.getGame().getDamageManager(); }
	
	public DamageManager() {}
	
	private GameDamage lastUsedCustomDamage = null;
	
	public void customDamage(GameEntity attacker, GameEntity receiver, CustomDamageType type, double damage) {
		customDamage(attacker, receiver, type, damage, new DamageModifier());
	}
	
	public void customDamage(GameEntity attacker, GameEntity receiver, CustomDamageType type, double damage, DamageModifier modifier) {
		GameDamage gameDamage = GameDamage.createDamage(attacker, receiver, type, damage);
		modifier.applyToDamage(gameDamage);
		
		if (lastUsedCustomDamage != null) {
			Bukkit.getLogger().severe("Last damage used was not null!?");
		}
		
	}
	
	public void processDamageEvent(EntityDamageEvent event) {
		GameEntity damagee = Game.getGame().getGameEntity(event.getEntity());
		NaturalDamageType type;
		
		switch (event.getCause()) {
			case CUSTOM: {
				if (lastUsedCustomDamage == null) {
					throw new IllegalStateException("Custom damage called but none stored in damage manager?");
				}
				GameDamage damage = lastUsedCustomDamage;
				lastUsedCustomDamage = null;
				damage.notifyEntities();
				damage.applyDamage(event);
				return;
			}
				
			default:
				throw new IllegalArgumentException("Cannot create GameDamage with event cause " + event.getCause());
			
			case ENTITY_ATTACK: {
				GameEntity damager = Game.getGame().getGameEntity(((EntityDamageByEntityEvent) event).getDamager());
				
				if ((damager instanceof MonsterPlayer && damagee instanceof AIEntity) ||
						(damagee instanceof MonsterPlayer && damager instanceof AIEntity)) {
					event.setCancelled(true);
					return;
				} // TODO Move to AIEntity
				
				GameDamage damage = GameDamage.createDamage(damager, damagee, NaturalDamageType.MELEE, event.getDamage());
				damage.notifyEntities();
				damage.applyDamage(event);
				return;
			}
			
			case PROJECTILE: {
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				GameEntity damager = Game.getGame().getGameEntity((Entity) proj.getShooter());
				
				GameDamage damage = GameDamage.createDamage(damager, damagee, NaturalDamageType.RANGED, event.getDamage(), proj);
				damage.notifyEntities();
				damage.applyDamage(event);
				return;
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
		GameDamage damage = GameDamage.createDamage(null, damagee, type, event.getDamage());
		type.applyDamage(damage); // TODO change to modifier
		damage.notifyEntities();
		damage.applyDamage(event);
	}
	
	
	// ==============================
	// ----------EXPLOSIONS----------
	// ==============================
	
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, double range, double damage, double kbStrength, DamageModifier modifier) {
		AOEDamage(receivers, attacker, type, attacker.getLocation(), range, damage, kbStrength, modifier);
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, double damage, double kbStrength) {
		AOEDamage(receivers, attacker, type, origin, range, damage, kbStrength, new DamageModifier());
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, double damage, double kbStrength, DamageModifier modifier) {
		AOEDamage(receivers, attacker, type, origin, range,
						(Vector v) -> damage,
						(Vector v) -> v.clone().multiply(kbStrength / Math.sqrt(Math.max(1, v.length())) ),
						modifier);
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, Function<Vector, Double> damageFunction, Function<Vector, Vector> knockbackFunction, DamageModifier modifier) {
		for (GameEntity receiver : receivers) {
			Vector offset = origin.subtract(receiver.getLocation()).toVector();
			if (offset.length() > range) continue;
			
			double damage = damageFunction.apply(offset);
			Vector knockback = knockbackFunction.apply(offset);
			modifier.addKnockback(knockback);
			
			customDamage(attacker, receiver, type, damage, modifier);
		}
	}
}
