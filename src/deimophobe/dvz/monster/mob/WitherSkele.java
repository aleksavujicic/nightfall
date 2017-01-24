package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.PlayerMonster;

/**
 * Created by Deimophobe on 20/01/17.
 */
class WitherSkele extends Mob {
	WitherSkele(Mob template, PlayerMonster monster) {
		super(template, monster);
	}
	
	@Override
	public WitherSkele clone(PlayerMonster monster) {
		return new WitherSkele(this, monster);
	}
}
