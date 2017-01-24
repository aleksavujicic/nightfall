package deimophobe.dvz;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 24/01/17.
 */
public interface PlayerOrAI {
	
	LivingEntity getEntity();
	
	String getDisplayName();
	
	double onHit(PlayerOrAI entity, DamageType type, double damage);
	double onGotHit(PlayerOrAI entity, DamageType type, double damage);
}
