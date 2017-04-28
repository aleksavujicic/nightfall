package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Ogre extends AbstractTypedMob {
	@Override
	protected MobType getType() {return MobType.OGRE;}
	
	Ogre(MonsterPlayer monster) {
		super(monster);
	}
}
