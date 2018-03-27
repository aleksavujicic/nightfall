package deimophobe.nightfall.monster;

import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;

import java.util.function.Function;

/**
 * Created by Deimophobe on 27/03/18.
 */
public enum SpawnMethod {
	SPAWN(mp -> GameMap.getCurrentMap().getCurrentMobspawn()),
	REBIRTH(MonsterPlayer::getRebirthLocation),
	NONE(MonsterPlayer::getLocation);
	
	private final Function<MonsterPlayer, Location> spawner;
	
	SpawnMethod(Function<MonsterPlayer, Location> spawner) {
		this.spawner = spawner;
	}
	
	public Location getSpawnPoint(MonsterPlayer monster) {
		return spawner.apply(monster);
	}
}
