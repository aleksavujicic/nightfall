package deimophobe.nightfall.bungee.event;

import deimophobe.nightfall.bungee.server.Game;
import net.ME1312.SubServers.Bungee.Host.SubServer;

/**
 * Created by Deimophobe on 18/12/17.
 */
public class GameStartEvent extends GameEvent {
	public GameStartEvent(Game game) {
		super(game);
	}
	
	public SubServer getServer() {
		return getGame().getServer();
	}
}
