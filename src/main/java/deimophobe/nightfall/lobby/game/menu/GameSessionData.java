package deimophobe.nightfall.lobby.game.menu;

import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/18.
 */
public class GameSessionData implements SessionData {
	private final Player player;
	
	public Player getPlayer() { return player; }
	
	GameSessionData(Player player) {
		this.player = player;
	}
}
