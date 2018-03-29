package deimophobe.nightfall.monster.doom;

import deimophobe.nightfall.monster.mob.MobType;

/**
 * Created by Deimophobe on 8/10/17.
 */

@DoomMeta(
		cycleTime = 20,
		title = "Tickers",
		subtitles = {
				"Tick.",
				"Tick. Tock.",
				"Tick. Tock. Tick.",
				"Tick. Tock. Tick. Tock.",
				"Boom",
		},
		regularMobs = { MobType.TICKER }
)
class TickerDoom extends AnnotatedDoom {}
