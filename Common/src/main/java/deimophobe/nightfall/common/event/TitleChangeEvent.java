package deimophobe.nightfall.common.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Deimophobe on 25/12/17.
 */
public class TitleChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private final Player player;
	private final String newTitle;
	public Player getPlayer() {return player;}
	public String getNewTitle() {return newTitle;}
	
	private boolean updateDisplayName;
	public boolean shouldUpdateDisplayName() {return updateDisplayName;}
	public void setUpdateDisplayName(boolean updateDisplayName) {this.updateDisplayName = updateDisplayName;}
	
	public TitleChangeEvent(Player player, String newTitle) {
		this.player = player;
		this.newTitle = newTitle;
		this.updateDisplayName = false;
	}
}