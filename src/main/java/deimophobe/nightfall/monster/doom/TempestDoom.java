package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.mob.MobType;

/**
 * Created by Deimophobe on 29/03/18.
 */
@DoomMeta(
		title = "The Tempest",
		subtitles = {
				"A Storm Gathers..."
		},
		regularMobs = { MobType.ZEPHYR }
)
public class TempestDoom extends AnnotatedDoom {
	@Override
	public void startDoom() {
		super.startDoom();
	}
}
