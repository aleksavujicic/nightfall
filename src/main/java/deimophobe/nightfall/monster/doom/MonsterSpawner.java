package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 29/03/18.
 */
@FunctionalInterface
interface MonsterSpawner {
	void spawnMonster(MonsterPlayer monster);
}
