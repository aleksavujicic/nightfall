package deimophobe.nightfall.monster.doom;


import deimophobe.nightfall.game.Curse;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.monster.mob.MobType;


@DoomMeta(
		title = "Blizzard",
		subtitles = {
				"A freezing wind",
				"summons fearsome beasts"
		},

		regularMobs = { MobType.POLARBEAR }
)

public class BlizzardDoom extends AnnotatedDoom {

	@Override
	public void startDoom() {
		super.startDoom();

		Game.getGame().addCurse(Curse.BLIZZARD,2*(60*20));
	}
}

