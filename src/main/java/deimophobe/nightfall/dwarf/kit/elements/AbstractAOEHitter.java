package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.damage.GameDamage;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.ai.AIEntity;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 10/03/17.
 */
abstract class AbstractAOEHitter extends AbstractItem {
	
	AbstractAOEHitter(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		
		if (damageFromItem(damage)) {
			MonsterEntity monster = damage.getMonster();
			
			Location center = monster.getLocation();
			double radius = getRadius();
			for (GameEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
				if (entity == monster)
					continue;
				
				if (center.distance(entity.getLocation()) <= radius) {
					GameDamage newDamage = entity.createDamage(dwarf, CustomDamageType.HAMMER_AOE, getDamageToMonster(entity));
					
					if (entity instanceof AIEntity)
						newDamage.setKnockback(0, 0.4, 0);
					
					newDamage.setNoDmgTicks(10);
					
					newDamage.fire();
				}
			}
		}
	}
	
	protected abstract double getDamageToMonster(GameEntity entity);
	protected abstract double getRadius();
}
