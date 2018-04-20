package deimophobe.nightfall.bungee.event;

import deimophobe.nightfall.bungee.server.Game;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Deimophobe on 18/12/17.
 */
public abstract class GameEvent extends Event {
	private final Game game;
	
	public Game getGame() {
		return game;
	}
	
	public GameEvent(Game game) {
		this.game = game;
	}
}
