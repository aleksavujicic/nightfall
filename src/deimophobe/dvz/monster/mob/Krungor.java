package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.PlayerMonster;

/**
 * Created by Deimophobe on 26/01/17.
 */
class Krungor extends Mob {
	Krungor(Mob template, PlayerMonster monster) {
		super(template, monster);
	}
	
	@Override
	public Krungor clone(PlayerMonster monster) {
		return new Krungor(this, monster);
	}
}
