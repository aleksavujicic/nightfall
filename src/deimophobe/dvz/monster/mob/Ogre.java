package deimophobe.dvz.monster.mob;

import deimophobe.dvz.monster.PlayerMonster;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Ogre extends Mob {
	Ogre(Mob template, PlayerMonster monster) {
		super(template, monster);
	}
	
	@Override
	public Ogre clone(PlayerMonster monster) {
		return new Ogre(this, monster);
	}
}
