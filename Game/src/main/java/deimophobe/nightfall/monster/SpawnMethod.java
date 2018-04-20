package deimophobe.nightfall.monster;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;

import java.util.function.Function;

/**
 * Created by Deimophobe on 27/03/18.
 */
public enum SpawnMethod {
	SPAWN(mp -> GameMap.getCurrentMap().getCurrentMobspawn()),
	DOOM(mp -> GameMap.getCurrentMap().getCurrentMobspawn()),
	NONE(MonsterPlayer::getLocation),
	
	REBIRTH(mp -> {
		if (mp.canRebirth()) {
			return mp.getRebirthLocation();
		} else {
			NightfallPlugin.logger().warning("Tried to rebirth player '" + mp.getName() + "' with no rebirth spot.");
			return GameMap.getCurrentMap().getCurrentMobspawn();
		}
	}),
	
	;
	
	private final Function<MonsterPlayer, Location> spawner;
	
	SpawnMethod(Function<MonsterPlayer, Location> spawner) {
		this.spawner = spawner;
	}
	
	public Location getSpawnPoint(MonsterPlayer monster) {
		return spawner.apply(monster);
	}
	
}
