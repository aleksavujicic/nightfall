package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.ai.AIEntity;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.ai.AIManager;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Hammer extends Sword {
	
	private int updateCount = 0;
	
	
	Hammer(Dwarf dwarf) {
		super(dwarf, SwordType.HAMMER, 60);
	}
	
	//TODO?
	//@Override
	//public void onKill() {}
	
	private boolean hasHit = false;
	private static final double AOE_RADIUS = 2.5;
	@Override
	public double onHit(GameEntity monster, double damage) {
		if (hasHit || monster == null) return damage;
		
		hasHit = true;
		final double monsterDmg = (dwarf.hasProc() ? 20 : 5);
		final double aiDmg = (dwarf.hasProc() ? 40 : 20);
		Location center = monster.getLocation();
		for (MonsterPlayer monsterPlayer : MobManager.getManager().getMobs()) {
			if (monsterPlayer == monster) {
				damage += monsterDmg;
				continue;
			}
			if (center.distance(monsterPlayer.getLocation()) <= AOE_RADIUS)
				monsterPlayer.customDamage(dwarf, DamageType.HAMMER_AOE, monsterDmg);
		}
		for (AIEntity ai : AIManager.getManager().getAIs()) {
			if (ai == monster) {
				damage += aiDmg;
				continue;
			}
			if (center.distance(ai.getLocation()) <= AOE_RADIUS)
				ai.customDamage(dwarf, DamageType.HAMMER_AOE, aiDmg);
		}
		reduceCooldown(20);
		return damage;
	}
	
	@Override
	public void update() {
		hasHit = false;
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
				dwarf.repairArmour(3);
				dwarf.regenMana(1);
				
				if (cooldown >= maxCooldown) cooldown = maxCooldown;
			}
		}
	}
	
	@Override
	public float fractionComplete() {
		return (float)cooldown/60;
	}
}
