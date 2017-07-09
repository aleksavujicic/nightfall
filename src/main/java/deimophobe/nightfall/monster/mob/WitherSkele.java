package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class WitherSkele extends SkeletonMob {
	
	@Override protected double getPower() {return 40;}
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
	public double onHit(Dwarf dwarf, DamageType type, double damage) {
		if (type.isArrow()) {
			damageBoost += 5;
			dwarf.getArmour().damage((int) damageBoost*3 + 20);
			monster.heal(5);
			return getPower() + damageBoost;
		} else {
			return damage;
		}
	}
}
