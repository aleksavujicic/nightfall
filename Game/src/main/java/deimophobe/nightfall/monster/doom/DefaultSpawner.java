package deimophobe.nightfall.monster.doom;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.monster.MobCreator;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.SpawnRegistry;
import deimophobe.nightfall.monster.mob.MobType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/03/18.
 */
class DefaultSpawner implements MonsterSpawner {
	private final PeekingIterator<MobCreator<?>> specialIterator;
	private final Consumer<MonsterPlayer> regularSpawner;
	
	DefaultSpawner(SpecialSpawn[] specialSpawns, NamedSpecialSpawn[] namedSpecialSpawns, MobType[] regulars) {
		GameSize currentSize = Game.getGame().getGameSize();
		List<MobCreator<?>> specialTypes = new ArrayList<>();
		for (SpecialSpawn specialSpawn : specialSpawns) {
			if (currentSize.isAtLeast(specialSpawn.size())) {
				specialTypes.add(specialSpawn.special());
			}
		}
		
		SpawnRegistry registry = SpawnRegistry.getRegistry();
		for (NamedSpecialSpawn specialSpawn : namedSpecialSpawns) {
			if (currentSize.isAtLeast(specialSpawn.size())) {
				String creatorName = specialSpawn.special();
				if (registry.isValid(creatorName)) {
					MobCreator<?> creator = registry.getCreator(creatorName);
					specialTypes.add(creator);
				} else {
					NightfallPlugin.logger().severe("Unknown mob creator '" + creatorName + "' when trying to spawn doom.");
				}
			}
		}
		
		specialIterator = Iterators.peekingIterator(specialTypes.iterator());
		
		if (regulars.length == 0) {
			regularSpawner = mp -> {
				mp.spawnPrimaryMob(SpawnMethod.DOOM);
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
			MobCreator<?> creator = specialIterator.peek();
			boolean success = monster.spawnMob(creator, SpawnMethod.DOOM);
			if (success) specialIterator.next(); // Successfully spawned, remove special
		} else {
			regularSpawner.accept(monster);
		}
	}
}
