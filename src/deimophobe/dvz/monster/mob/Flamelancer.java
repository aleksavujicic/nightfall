package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.PlayerMonster;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Flamelancer extends Mob {
	Flamelancer(Mob template, PlayerMonster monster) {
		super(template, monster);
	}
	
	@Override
	public Flamelancer clone(PlayerMonster monster) {
		return new Flamelancer(this, monster);
	}
}
