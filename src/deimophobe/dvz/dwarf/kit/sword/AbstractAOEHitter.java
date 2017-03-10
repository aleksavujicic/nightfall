package deimophobe.dvz.dwarf.kit.sword;

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
abstract class AbstractAOEHitter extends Sword {
	
	private final double radius;
	
	AbstractAOEHitter(Dwarf dwarf, SwordType type, int maxCooldown, double radius) {
		super(dwarf, type, maxCooldown);
		this.radius = radius;
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		if (type.isCustom() || monster == null) return damage;
		
		Location center = monster.getLocation();
		for (GameEntity entity : MonsterManager.getManager().getMobsAndAIs()) {
			if (entity == monster) {
				damage += getDamageToMonster(entity);
				continue;
			}
			if (center.distance(entity.getLocation()) <= radius)
				entity.customDamage(dwarf, DamageType.HAMMER_AOE, getDamageToMonster(entity));
		}
		return damage;
	}
	
	protected abstract double getDamageToMonster(GameEntity entity);
}
