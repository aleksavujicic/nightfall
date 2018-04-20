package deimophobe.nightfall.common.event;/**
 * Created by Deimophobe on 25/12/17.
 */

import deimophobe.nightfall.common.cosmetic.hat.Hat;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HatChangeEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}

	private final Player player;
	private final Hat newHat;
	public Player getPlayer() {return player;}
	public Hat getNewHat() {return newHat;}

	private boolean updateHat;
	public boolean shouldUpdateHat() {return updateHat;}
	public void setUpdateHat(boolean updateHat) {this.updateHat = updateHat;}

	public HatChangeEvent(Player player, Hat newHat) {
		this.player = player;
		this.newHat = newHat;
		this.updateHat = false;
	}
}