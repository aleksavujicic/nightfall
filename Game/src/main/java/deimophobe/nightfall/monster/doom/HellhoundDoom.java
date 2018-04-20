package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.mob.MobType;

/**
 * Created by Deimophobe on 8/07/17.
 */

@DoomMeta(
		title = "Hellhounds",
		regularMobs = { MobType.HELLHOUND }
)
class HellhoundDoom extends AnnotatedDoom {
	@Override
	public void startDoom() {
		super.startDoom();
		MonsterManager.getManager().addSpawnEgg(5, "hellhound");
	}
}
