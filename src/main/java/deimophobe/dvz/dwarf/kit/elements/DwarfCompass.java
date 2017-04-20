package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.shrine.CompassLocation;
import deimophobe.dvz.shrine.ShrineManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Deimophobe on 31/03/17.
 */
class DwarfCompass extends AbstractItem {
	DwarfCompass(Dwarf dwarf) {super(dwarf);}
	
	private final static ItemStack ITEM = DwarvenItems.getItem("misc.compass", Slot.MAIN_HAND);
	@Override public ItemStack getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	private int nextIndex = 0;
	private boolean canUse = true;
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		if (!canUse) return false;
		
		// Get compass list
		List<CompassLocation> locations = ShrineManager.getManager().getCompassLocations();
		CompassLocation cl = locations.get(nextIndex);
		
		// Change index
		nextIndex = (nextIndex + 1) % locations.size();
		
		// Set location
		dwarf.sendMessage(ChatColor.LIGHT_PURPLE + "Compass is now pointing at: " + ChatColor.YELLOW + cl.getName());
		dwarf.getPlayer().setCompassTarget(cl.getLocation());
		
		// Lock to prevent 'double clicking'
		canUse = false;
		new BukkitRunnable() {
			@Override public void run() {canUse = true;}
		}.runTaskLater(Game.getGame().getPlugin(), 4);
		
		return true;
	}
}
