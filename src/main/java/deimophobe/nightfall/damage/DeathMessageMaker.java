package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GamePlayer;
import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Created by Deimophobe on 13/02/18.
 */
@FunctionalInterface
interface DeathMessageMaker {
	BaseComponent getDeathMessage(GamePlayer deadPlayer, LastMainDamage lastMainDamage);
	
	DeathMessageMaker DIED_MESSAGE = new ForcedDeathMessageMaker("died");
	DeathMessageMaker SLAIN_MESSAGE = new KeywordDeathMessageMaker("slain");
	DeathMessageMaker SIMPLE_DEATH_MESSAGE = (deadPlayer, lastMainDamage) -> {
		if (lastMainDamage.hasAttacker()) {
			return SLAIN_MESSAGE.getDeathMessage(deadPlayer, lastMainDamage);
		} else {
			return DIED_MESSAGE.getDeathMessage(deadPlayer, lastMainDamage);
		}
	};
}
