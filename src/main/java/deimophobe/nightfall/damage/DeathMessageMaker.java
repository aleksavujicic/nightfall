package deimophobe.nightfall.damage;

import net.md_5.bungee.api.chat.BaseComponent;

/**
 * Created by Deimophobe on 13/02/18.
 */
@FunctionalInterface
interface DeathMessageMaker {
	BaseComponent getDeathMessage(String playerName, LastMainDamage lastMainDamage);
	
	DeathMessageMaker DIED_MESSAGE = new ForcedDeathMessageMaker("died");
	DeathMessageMaker SLAIN_MESSAGE = new KeywordDeathMessageMaker("slain");
	DeathMessageMaker SIMPLE_DEATH_MESSAGE = (playerName, lastMainDamage) -> {
		if (lastMainDamage.hasAttacker()) {
			return SLAIN_MESSAGE.getDeathMessage(playerName, lastMainDamage);
		} else {
			return DIED_MESSAGE.getDeathMessage(playerName, lastMainDamage);
		}
	};
}
