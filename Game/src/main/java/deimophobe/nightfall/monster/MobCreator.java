package deimophobe.nightfall.monster;

import deimophobe.nightfall.monster.mob.Mob;

/**
 * Created by Deimophobe on 18/06/18.
 */
public interface MobCreator<T extends Mob> {
	T createMob(MonsterPlayer monster);
	String getName();
}
