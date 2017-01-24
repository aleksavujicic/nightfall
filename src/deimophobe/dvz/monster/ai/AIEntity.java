package deimophobe.dvz.monster.ai;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.PlayerOrAI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity implements PlayerOrAI {
	private final LivingEntity entity;
	@Override
	public LivingEntity getEntity() {
		return entity;
	}
	
	@Override
	public String getDisplayName() {
		return "TEMP AI PLACEHOLDER";
	}
	
	public AIEntity(LivingEntity entity) {
		this.entity = entity;
	}
	
	@Override
	public double onHit(PlayerOrAI entity, DamageType type, double damage) {
		return 15;
	}
	
	@Override
	public double onGotHit(PlayerOrAI entity, DamageType type, double damage) {
		return damage;
	}
}
