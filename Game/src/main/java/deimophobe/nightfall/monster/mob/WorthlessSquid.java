package deimophobe.nightfall.monster.mob;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 11/10/18.
 */
final class WorthlessSquid extends AbstractMob {
	WorthlessSquid(MonsterPlayer monster) {
		super(monster, MobType.SQUID);
	}
	
	@Override
	protected void teleportToSpawn(SpawnMethod spawnMethod) {
		if (spawnMethod == SpawnMethod.DOOM) {
			Location center = GameMap.getCurrentMap().getShrineCenter();
			monster.teleportTo(center);
		} else {
			super.teleportToSpawn(spawnMethod);
		}
	}
}
