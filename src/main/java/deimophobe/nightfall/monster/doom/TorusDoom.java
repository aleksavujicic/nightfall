package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.GameSize;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrade.GlobalUpgrade;

/**
 * Created by Deimophobe on 25/02/17.
 */
@DoomMeta(
		title = "Torus",
		subtitles = {
				"King of Minotaurs",
				"Lord of the Labyrinth"
		},
		specialMobs = {
				@SpecialSpawn(special = MobType.KRUNGOR, size = GameSize.MEDIUM)
		},
		regularMobs = { MobType.MINOTAUR }
)
class TorusDoom extends AnnotatedDoom {
	
	@Override
	public void startDoom() {
		GlobalUpgrade.KRUNGOR.unlock();
		super.startDoom();
	}
}
