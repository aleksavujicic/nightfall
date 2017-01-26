package deimophobe.dvz;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by Deimophobe on 24/01/17.
 */
public interface PlayerOrAI {
	
	LivingEntity getEntity();
	
	String getDisplayName();
	
	double onHit(PlayerOrAI entity, DamageType type, double damage);
	double onGotHit(PlayerOrAI entity, DamageType type, double damage);
	double onNaturalHit(EntityDamageEvent.DamageCause cause, double baseDmg);
}
