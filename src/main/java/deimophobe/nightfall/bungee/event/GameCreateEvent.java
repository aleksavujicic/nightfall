package deimophobe.nightfall.bungee.event;

import deimophobe.nightfall.bungee.map.GameMap;
import deimophobe.nightfall.bungee.server.Game;
import deimophobe.nightfall.bungee.server.GameSettings;

/**
 * Created by Deimophobe on 18/12/17.
 */
public class GameCreateEvent extends GameEvent {
	public GameCreateEvent(Game game) {
		super(game);
	}
	
	public GameMap getMap() {
		return getGame().getMap();
	}
	
	public GameSettings getSettings() {
		return getGame().getSettings();
	}
}
