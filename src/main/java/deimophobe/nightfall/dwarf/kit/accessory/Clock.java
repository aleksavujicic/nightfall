package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 12/02/18.
 */
public class Clock extends AbstractItem {
	public Clock(Dwarf dwarf) {super(dwarf);}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "clock");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public KitGiveType getGiveType() {return KitGiveType.START;}
	
	private final ComplexCooldown useCooldown = new ComplexCooldown(4, this::showTime);
	
	@Override
	public void update(boolean quartSec, boolean halfSec, boolean sec, boolean doubleSec, boolean quadSec) {
		super.update(quartSec, halfSec, sec, doubleSec, quadSec);
		useCooldown.update();
	}
	
	@Override
	public boolean onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		super.onUse(action, clickedBlock, blockFace);
		useCooldown.tryUse();
		return true;
	}
	
	private void showTime() {
		long time = dwarf.getWorld().getTime();
		int gameHour = (int) (time % 24000)/1000;
		boolean isNight = gameHour >= 14;
		
		int hour = (gameHour + 6) % 24;
		boolean am = (hour < 12);
		
		int displayHour;
		if (hour == 0) {
			displayHour = 12;
		} else if (hour > 12) {
			displayHour = hour - 12;
		} else {
			displayHour = hour;
		}
		
		String amMessage = (am ? "AM" : "PM");
		ChatColor textColour = (isNight ? ChatColor.YELLOW : ChatColor.DARK_PURPLE);
		ChatColor highlightColour = (isNight ? ChatColor.AQUA : ChatColor.LIGHT_PURPLE);
		dwarf.sendTitleMessage(textColour + "The time is now " + highlightColour + displayHour + amMessage);
	}
}
