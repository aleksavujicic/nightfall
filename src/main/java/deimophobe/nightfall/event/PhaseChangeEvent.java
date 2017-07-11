package deimophobe.nightfall.event;

import deimophobe.nightfall.Phase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Deimophobe on 11/07/17.
 */
public class PhaseChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Phase phase;
	
	public PhaseChangeEvent(Phase phase) {
		this.phase = phase;
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
