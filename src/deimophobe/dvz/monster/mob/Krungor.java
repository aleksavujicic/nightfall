package deimophobe.dvz.monster.mob;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Krungor extends Mob {
	Krungor(MonsterPlayer monster) {
		super(monster, MobType.KRUNGOR);
	}
	
	@Override
	public double onGotHit(Dwarf dwarf, DamageType type, double damage) {
		return damage/2;
	}
}
