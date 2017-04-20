package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 2/02/17.
 */
class Zombie extends AbstractTypedMob {
	
	@Override protected MobType getType() {return MobType.ZOMBIE;}
	
	protected Zombie(MonsterPlayer mons) {
		super(mons);
	}
}
