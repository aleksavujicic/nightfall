package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.GameSize;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.mob.MobType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/03/18.
 */
class DefaultSpawner implements MonsterSpawner {
	private final Iterator<MobType> specialIterator;
	private final Consumer<MonsterPlayer> regularSpawner;
	
	DefaultSpawner(SpecialSpawn[] specialSpawns, MobType[] regulars) {
		GameSize currentSize = Game.getGame().getGameSize();
		List<MobType> specialTypes = new ArrayList<>();
		for (SpecialSpawn specialSpawn : specialSpawns) {
			if (currentSize.isAtLeast(specialSpawn.size())) {
				specialTypes.add(specialSpawn.special());
			}
		}
		
		specialIterator = specialTypes.iterator();
		
		if (regulars.length == 0) {
			regularSpawner = mp -> {
				mp.spawnMob(mp.getPrimaryMob(), SpawnMethod.DOOM);
			};
		} else {
			regularSpawner = mp -> {
				mp.spawnMob(Misc.getRandom(regulars), SpawnMethod.DOOM);
			};
		}
	}
	
	@Override
	public void spawnMonster(MonsterPlayer monster) {
		if (specialIterator.hasNext()) {
			monster.spawnMob(specialIterator.next(), SpawnMethod.DOOM);
		} else {
			regularSpawner.accept(monster);
		}
	}
}
