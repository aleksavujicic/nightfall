package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;
import deimophobe.nightfall.monster.mob.MobType;

/**
 * Created by Deimophobe on 27/02/18.
 */
@DoomMeta(
		title = "Ogre Magi",
		subtitles = {
				"Curse of Doom",
		},
		specialMobs = {
				@SpecialSpawn(special = MobType.MAGI, size = GameSize.SMALL),
				@SpecialSpawn(special = MobType.MAGI, size = GameSize.MEDIUM),
				@SpecialSpawn(special = MobType.MAGI, size = GameSize.LARGE),
				@SpecialSpawn(special = MobType.MAGI, size = GameSize.HUGE)
		}
)
public class OgreMagiDoom extends AnnotatedDoom {
	
	@Override
	public void startDoom() {
		super.startDoom();
		
		Game.getGame().addCurse(Curse.DOOM, 90);
		Game.getGame().addCurse(Curse.SUPER_DOOM, 11);
	}
}
