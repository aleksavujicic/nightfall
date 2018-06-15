package deimophobe.nightfall.event;/**
 * Created by Deimophobe on 16/06/18.
 */

import deimophobe.nightfall.damage.DwarfDamage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DwarfDamageEvent extends Event {
	private final DwarfDamage damage;
	
	public DwarfDamageEvent(DwarfDamage damage) {
		this.damage = damage;
	}
	
	public DwarfDamage getDamage() {
		return damage;
	}
	
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
}