package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GamePlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

/**
 * Created by Deimophobe on 23/02/18.
 */
class ForcedDeathMessageMaker implements DeathMessageMaker {
	private final String message;
	
	ForcedDeathMessageMaker(String message) {
		this.message = message;
	}
	
	@Override
	public BaseComponent getDeathMessage(String playerName, LastMainDamage lastMainDamage) {
		return new TextComponent(playerName + " " + message + ChatColor.RESET + ".");
	}
}
