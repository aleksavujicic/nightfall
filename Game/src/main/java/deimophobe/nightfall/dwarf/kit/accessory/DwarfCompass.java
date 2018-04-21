package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.map.CompassLocation;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Created by Deimophobe on 31/03/17.
 */
public class DwarfCompass extends AbstractItem {
	public DwarfCompass(Dwarf dwarf) {super(dwarf);}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "compass");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	private int nextIndex = 0;
	private boolean canUse = true;
	
	@Override
	public boolean onUse(ClickType click, Block block, BlockFace face) {
		if (!canUse) return false;
		
		// Get compass list
		List<CompassLocation> locations = GameMap.getCurrentMap().getCompassLocations();
		
		// Change index
		if (click.isRightClick()) {
			if (dwarf.getPlayer().isSneaking()) {
				nextIndex = (nextIndex == 0 ? locations.size() - 1 : nextIndex - 1);
			} else {
				nextIndex = (nextIndex + 1) % locations.size();
			}
		}
		
		// Get new compass location
		CompassLocation cl = locations.get(nextIndex);
		
		// Set location
		//dwarf.sendMessage(ChatColor.LIGHT_PURPLE + "Compass is now pointing at: " + ChatColor.YELLOW + cl.getName());
		//String leftArrow = (wentLeft ? ChatColor.GOLD : ChatColor.WHITE ) + "←";
		//String rightArrow = (!wentLeft ? ChatColor.GOLD : ChatColor.WHITE ) + "→";
		dwarf.sendTitleMessage(ChatColor.LIGHT_PURPLE + cl.getName());
		dwarf.getPlayer().setCompassTarget(cl.getLocation());
		
		// Lock to prevent 'double clicking'
		canUse = false;
		new BukkitRunnable() {
			@Override public void run() {canUse = true;}
		}.runTaskLater(NightfallPlugin.getPlugin(), 4);
		
		return true;
	}
}
