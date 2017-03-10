package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Hammer extends AbstractAOEHitter {
	
	
	
	Hammer(Dwarf dwarf) {
		super(dwarf, SwordType.HAMMER, 60, 2);
	}
	
	@Override
	public double onHit(GameEntity monster, DamageType type, double damage) {
		reduceCooldown(20);
		return super.onHit(monster, type, damage);
	}
	
	private int updateCount = 0;
	@Override
	public void update() {
		if (!dwarf.isBlocking()) {
			if (cooldown > 0)
				cooldown -= 1;
		} else {
			cooldown += 1;
			if (cooldown > maxCooldown) cooldown = maxCooldown;
			dwarf.updateCooldownBar();
			
			updateCount += 1;
			if (updateCount < 5) return;
			updateCount = 0;
			
			if (cooldown == maxCooldown) {
				dwarf.playSound("entity.experience_orb.pickup", 10f, 0.5f, false);
				dwarf.repairArmour(5);
				dwarf.regenMana(1);
				
				if (cooldown >= maxCooldown) cooldown = maxCooldown;
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float)cooldown/60;
	}
	
	@Override
	protected double getDamageToMonster(GameEntity entity) {
		if (entity instanceof MonsterPlayer) {
			return (dwarf.hasProc() ? 20 : 5);
		} else if (entity instanceof AIEntity) {
			return  (dwarf.hasProc() ? 40 : 20);
		}
		
		return 0;
	}
}
