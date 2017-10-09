package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DwarfDamage;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class WitherSkele extends SkeletonMob {
	
	@Override protected double getPower() {return 30;}
	private double damageBoost = 0;
	
	WitherSkele(MonsterPlayer monster) {
		super(monster, MobType.WITHERSKELE);
	}
	
	@Override
	public void onSpawn() {
		super.onSpawn();
		giveArrows(128);
	}
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		if (sec && damageBoost > 0)
			damageBoost = Math.max(damageBoost - 1, 0);
	}
	
	@Override
	public void onDamageAttack(DwarfDamage damage) {
		super.onDamageAttack(damage);
		if (damage.hasArrow()) {
			damageBoost = Math.min(damageBoost + 10, 50);
			damage.setArmourShred((int) damageBoost*3 + 100);
			damage.getDamage().addBoost(damageBoost*3);
			monster.heal(5);
		}
	}
}
