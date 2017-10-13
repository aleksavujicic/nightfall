package deimophobe.nightfall.damage;

import deimophobe.nightfall.ArrowMisc;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.damage.type.NaturalDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
		
	void customDamage(GameDamage damage, boolean force) {
		LivingEntity receiver = damage.getReceiver().getEntity();
		if (force || receiver.getNoDamageTicks() == 0) {
			if (lastUsedCustomDamage != null) {
				Bukkit.getLogger().severe("Last damage used was not null!? Damage: \n" + lastUsedCustomDamage.toString());
			}
			
			lastUsedCustomDamage = damage;
			if (force) receiver.setNoDamageTicks(0);
			receiver.damage(100);
			
			damage.applyNoDmgTicks();
		}
	}
	
	public void processDamageEvent(EntityDamageEvent event) {
		GameDamage damage = getDamageFromEvent(event);
		damage.activateTrigger();
		damage.notifyEntities();
		damage.applyDamage(event);
	}
	
	private GameDamage getDamageFromEvent(EntityDamageEvent event) {
		GameEntity receiver = Game.getGame().getGameEntity(event.getEntity());
		switch (event.getCause()) {
			case CUSTOM: {
				if (lastUsedCustomDamage == null) {
					throw new IllegalStateException("Custom damage called but none stored in damage manager?");
				}
				GameDamage damage = lastUsedCustomDamage;
				lastUsedCustomDamage = null;
				return damage;
			}
			
			case ENTITY_ATTACK: {
				GameEntity attacker = Game.getGame().getGameEntity(((EntityDamageByEntityEvent) event).getDamager());
				return GameDamage.createDamage(attacker, receiver, NaturalDamageType.MELEE, event.getDamage());
			}
			
			case PROJECTILE: {
				Projectile proj = (Projectile) ((EntityDamageByEntityEvent) event).getDamager();
				double damage;
				if (proj instanceof Arrow)
					damage = ArrowMisc.getArrowDamage((Arrow) proj);
				else
					damage = event.getDamage();
				GameEntity attacker = Game.getGame().getGameEntity((Entity) proj.getShooter());
				
				return GameDamage.createDamage(attacker, receiver, NaturalDamageType.RANGED, damage, proj);
			}
			
			default: {
				NaturalDamageType type = NaturalDamageType.getTypeFromEventCause(event.getCause());
				GameDamage damage = GameDamage.createDamage(null, receiver, type, event.getDamage());
				type.applyDamage(damage); // TODO change to modifier
				return damage;
			}
		}
	}
	
	
	// ==============================
	// ----------EXPLOSIONS----------
	// ==============================

	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, double damage, double kbStrength) {
		AOEDamage(receivers, attacker, type, origin, range, damage, kbStrength, new DamageModifier(), false);
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, double damage, double kbStrength, boolean force) {
		AOEDamage(receivers, attacker, type, origin, range, damage, kbStrength, new DamageModifier(), force);
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, double damage, double kbStrength, DamageModifier modifier, boolean force) {
		AOEDamage(receivers, attacker, type, origin, range,
						(Vector v) -> damage,
						(Vector v) -> v.clone().multiply(kbStrength / Math.sqrt(Math.max(1, v.length())) ),
						modifier, force);
	}
	
	public void AOEDamage(Collection<? extends GameEntity> receivers, GameEntity attacker, CustomDamageType type, Location origin, double range, Function<Vector, Double> damageFunction, Function<Vector, Vector> knockbackFunction, DamageModifier modifier, boolean force) {
		for (GameEntity receiver : receivers) {
			Vector offset = receiver.getLocation().subtract(origin).toVector();
			if (offset.length() > range) continue;
			
			double damage = damageFunction.apply(offset);
			Vector knockback = knockbackFunction.apply(offset);
			modifier.addKnockback(knockback);
			
			GameDamage gameDamage = GameDamage.createDamage(attacker, receiver, type, damage);
			modifier.applyToDamage(gameDamage);
			gameDamage.fire(force);
		}
	}

	public void DwarfAOEDamage(GameEntity attacker, CustomDamageType type, Location origin, double range, Function<Vector, Double> damageFunction, Function<Vector, Vector> knockbackFunction, DamageModifier modifier, boolean force, int armorShred) {
		for (Dwarf dwarf : DwarfManager.getManager().getDwarves()) {
			Vector offset = dwarf.getLocation().subtract(origin).toVector();
			if (offset.length() > range) continue;

			double damage = damageFunction.apply(offset);
			Vector knockback = knockbackFunction.apply(offset);
			modifier.addKnockback(knockback);

			DwarfDamage aoeDamage = dwarf.createDamage(attacker, type, damage);
			modifier.applyToDamage(aoeDamage);
			aoeDamage.setArmourShred(armorShred);
			aoeDamage.fire(force);
		}
	}
}
