package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameSize;

/**
 * Created by Deimophobe on 27/02/18.
 */
@DoomMeta(
		title = "Ogre Magi",
		subtitles = {
				"Curse of Doom",
		},
		namedSpecialMobs = {
				@NamedSpecialSpawn(special = "magi", size =  GameSize.SMALL),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.MEDIUM),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.LARGE),
				@NamedSpecialSpawn(special = "magi", size =  GameSize.HUGE),
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
