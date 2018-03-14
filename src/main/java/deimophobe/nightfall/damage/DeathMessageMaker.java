package deimophobe.nightfall.damage;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Deimophobe on 13/02/18.
 */
@FunctionalInterface
public interface DeathMessageMaker {
	BaseComponent getDeathMessage(TextComponent playerName, LastMainDamage lastMainDamage);
	
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
