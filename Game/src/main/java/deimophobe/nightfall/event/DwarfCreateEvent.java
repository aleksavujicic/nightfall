package deimophobe.nightfall.event;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Deimophobe on 11/07/17.
 */
public class DwarfCreateEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private final Dwarf dwarf;
	private Location spawnLocation = GameMap.getCurrentMap().getDwarfSpawn();
	
	
	public DwarfCreateEvent(Dwarf dwarf) {
		this.dwarf = dwarf;
	}
	
	
	public Dwarf getDwarf() {
		return dwarf;
	}
	public Location getSpawnLocation() { return spawnLocation; }
	public void setSpawnLocation(Location spawnLocation) { this.spawnLocation = spawnLocation; }
}
