package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.GameEntity;
import deimophobe.dvz.damage.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.ai.AIEntity;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 10/03/17.
 */
abstract class AbstractAOEHitter extends AbstractItem {
	
	AbstractAOEHitter(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		if (type != DamageType.REGULAR_MELEE || monster == null) return damage;
		
		Location center = monster.getLocation();
		double radius = getRadius();
		for (GameEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (entity == monster)
				continue;
			
			if (center.distance(entity.getLocation()) <= radius) {
				entity.customDamage(dwarf, DamageType.HAMMER_AOE, getDamageToMonster(entity));
				
				if (entity instanceof AIEntity)
					entity.setVelocity(0, 0.4, 0);
			}
		}
		return Math.max(damage, getDamageToMonster(monster));
	}
	
	protected abstract double getDamageToMonster(GameEntity entity);
	protected abstract double getRadius();
}
