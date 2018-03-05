package deimophobe.nightfall.dwarf.kit.melee;

import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.damage.MonsterDamage;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Created by Deimophobe on 10/03/17.
 */
public abstract class AbstractAOEHitter extends AbstractItem {
	
	private final ComplexCooldown hitter = new ComplexCooldown(10, this::hit);
	
	protected AbstractAOEHitter(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		hitter.update();
	}

	private void hit() {
		Location center = dwarf.getLocation().add(dwarf.getLocation().getDirection().multiply(1.5));
		for (MonsterEntity entity : MonsterManager.getManager().getAliveMobsAndAIs()) {
			if (entity.distanceTo(center) <= getRadius(entity)) {
				double rawDamage = getDamageToMonster(entity) + dwarf.getBonusMeleeDamage()/2;
				MonsterDamage damage = entity.createDamage(dwarf, GameDamageType.HAMMER_AOE, rawDamage);
				
				if (entity.isAI()) {
					damage.setKnockback(0, 0.4, 0);
				} else {
					damage.setKnockbackFromMelee();
					damage.multiplyKnockback(0.7);
				}
				
				if (dwarf.hasProc())
					damage.setProc(true);
				
				damage.setNoDmgTicks(5);
				damage.fire();
			}
		}
		
		center.getWorld().spawnParticle(Particle.SWEEP_ATTACK, center.add(0, 1, 0), 1, 0,0,0);
	}
	
	@Override
	public void onDamageAttack(MonsterDamage damage) {
		super.onDamageAttack(damage);
		if (damageFromItem(damage)) {
			damage.cancel();
			hitter.tryUse();
		}
	}
	
	protected abstract double getDamageToMonster(MonsterEntity entity);
	protected abstract double getRadius(MonsterEntity entity);
}
