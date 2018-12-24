package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.cooldown.LifetimeExpireable;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.ai.AIManager;
import deimophobe.nightfall.monster.ai.AIType;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.util.Util;
import org.bukkit.Location;


/**
 * Created by Deimophobe on 11/10/18.
 */
@DoomMeta(
		title = "Worthless Squids",
		subtitles = "They're kinda worthless",
		
		regularMobs = MobType.SQUID
)
class SquidDoom extends AnnotatedDoom {
	
	@Override
	public void startDoom() {
		super.startDoom();
		int numMobs = MonsterManager.getManager().getNumberOfPlayers();
		int numSquids = (int) (5 * Math.sqrt(numMobs) + 10);
		
		Location spawnSpot = GameMap.getCurrentMap().getShrineCenter();
		AIManager aiManager = AIManager.getManager();
		
		Util.doNTimes(numSquids, () -> {
			double dx = Misc.randomDouble(-5, 5);
			double dy = Misc.randomDouble(-1, 1);
			double dz = Misc.randomDouble(-5, 5);
			
			Location spawn = spawnSpot.clone().add(dx, dy, dz);
			aiManager.spawnAI(AIType.SQUID, spawn);
		});
	}
}
