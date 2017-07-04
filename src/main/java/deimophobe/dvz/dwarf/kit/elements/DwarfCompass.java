package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.map.CompassLocation;
import deimophobe.dvz.map.GameMap;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Deimophobe on 31/03/17.
 */
class DwarfCompass extends AbstractItem {
	DwarfCompass(Dwarf dwarf) {super(dwarf);}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("misc.compass", Slot.MAIN_HAND);
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() {return KitGiveType.COMPASS;}
	
	private int nextIndex = 0;
	private boolean canUse = true;
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		if (!canUse) return false;
		
		// Get compass list
		List<CompassLocation> locations = GameMap.getCurrentMap().getCompassLocations();
		
		// Change index
		if (dwarf.getPlayer().isSneaking())
			nextIndex = (nextIndex == 0 ? locations.size() - 1 : nextIndex - 1);
		else
			nextIndex = (nextIndex + 1) % locations.size();
		
		// Get new compass location
		CompassLocation cl = locations.get(nextIndex);
		
		// Set location
		dwarf.sendMessage(ChatColor.LIGHT_PURPLE + "Compass is now pointing at: " + ChatColor.YELLOW + cl.getName());
		dwarf.getPlayer().setCompassTarget(cl.getLocation());
		
		// Lock to prevent 'double clicking'
		canUse = false;
		new BukkitRunnable() {
			@Override public void run() {canUse = true;}
		}.runTaskLater(DvZPlugin.getPlugin(), 4);
		
		return true;
	}
}
