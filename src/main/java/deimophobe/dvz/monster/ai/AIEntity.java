package deimophobe.dvz.monster.ai;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.entity.Creature;

import java.util.UUID;

/**
 * Created by Deimophobe on 24/01/17.
 */
public class AIEntity extends GameEntity<Creature> {
	
	public AIEntity(Creature aiEntity) {
		super(aiEntity);
	}
	
	@Override
	public double onHit(GameEntity entity, DamageType type, double damage) {
		if (entity instanceof  Dwarf) {
			((Dwarf) entity).getArmour().damage(10);
			return 15;
		} else {
			return damage;
		}
	}
	
	@Override
	public double onGotHit(GameEntity entity, DamageType type, double damage) {
		if (type == null) return damage;
		
		damage = type.getMobDamage(damage);
		if (damage == -1)
			return -1;
		
		damage *= 0.2;
		
		return damage;
	}
	
	public void setTarget(Dwarf dwarf) {
		entity.setTarget(dwarf.getPlayer());
	}
	
	boolean hasTarget() {
		return entity.getTarget() != null;
	}
	
	boolean isDead() {
		return entity.isDead();
	}
	
	UUID getUniqueId() {
		return entity.getUniqueId();
	}
}
