package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 10/03/17.
 */
abstract class AbstractAOEHitter extends AbstractItem {
	
	private final double radius;
	
	AbstractAOEHitter(Dwarf dwarf, double radius) {
		super(dwarf);
		this.radius = radius;
	}
	
	@Override
	public double onSelfHit(GameEntity monster, DamageType type, double damage) {
		if (type != DamageType.REGULAR_MELEE || monster == null) return damage;
		
		Location center = monster.getLocation();
		for (GameEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (entity == monster) {
				damage += getDamageToMonster(entity);
				continue;
			}
			
			if (center.distance(entity.getLocation()) <= radius) {
				entity.customDamage(dwarf, DamageType.HAMMER_AOE, getDamageToMonster(entity));
				
				if (entity instanceof AIEntity)
					entity.setVelocity(0, 0.3, 0);
			}
		}
		return damage;
	}
	
	protected abstract double getDamageToMonster(GameEntity entity);
}
