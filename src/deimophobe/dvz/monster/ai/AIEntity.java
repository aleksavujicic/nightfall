package deimophobe.dvz.monster.ai;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.entity.Creature;

import java.util.UUID;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity extends GameEntity {
	private final Creature aiEntity;
	
	public AIEntity(Creature aiEntity) {
		super(aiEntity);
		this.aiEntity = aiEntity;
	}
	
	@Override
	public double onHit(GameEntity entity, DamageType type, double damage) {
		if (entity instanceof  Dwarf) {
			((Dwarf) entity).damageArmour(10);
			return 15;
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (type == null) return damage;
		
		if (type.isMobImmune()) return -1;
		
		if (type.isMelee()) damage *= 1d/5;
		if (type.isRanged()) damage *= 1d/3;
		
		return damage;
	}
	
	void kill() {
		aiEntity.damage(1000);
	}
	
	boolean hasTarget() {
		return aiEntity.getTarget() != null;
	}
	
	boolean isDead() {
		return aiEntity.isDead();
	}
	
	UUID getUniqueId() {
		return aiEntity.getUniqueId();
	}
}
