package deimophobe.nightfall.event;/**
 * Created by Deimophobe on 7/06/18.
 */

import deimophobe.nightfall.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event {
	private final Game game;
	
	public GameStartEvent(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}
	
	
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
}