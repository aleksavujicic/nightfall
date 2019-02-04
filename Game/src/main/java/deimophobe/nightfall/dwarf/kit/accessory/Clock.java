package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.cooldown.ComplexCooldown;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.kit.AbstractItem;
import deimophobe.nightfall.dwarf.kit.KitPieceType;
import deimophobe.nightfall.dwarf.kit.PickupType;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 12/02/18.
 */
public class Clock extends AbstractItem {
	public Clock(Dwarf dwarf, KitPieceType type) {
		super(dwarf, type);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("accessory", "clock");
	@Override public CustomItem getItem() { return ITEM; }
	@Override public PickupType getPickupType() {return PickupType.START;}
	
	private final ComplexCooldown useCooldown = new ComplexCooldown(4, this::showTime);
	
	@Override
	public void update() {
		super.update();
		useCooldown.update();
	}
	
	@Override
	public boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace) {
		super.onUse(click, clickedBlock, blockFace);
		useCooldown.tryUse();
		return true;
	}
	
	private void showTime() {
		long time = dwarf.getWorld().getTime();
		int timeDay = (int) time % 24000;
		boolean isNight = (12500 <= timeDay && timeDay <= 23000);
		String timeDescription;
		if (timeDay >= 22500 || timeDay <= 1500) {
			timeDescription = "dawn";
		} else if (timeDay <= 5000) {
			timeDescription = "morning";
		} else if (timeDay <= 11500) {
			timeDescription = "noon";
		} else if (timeDay < 13500) {
			timeDescription = "dusk";
		} else if (timeDay == 13500) {
			timeDescription = Misc.getNightfallText();
		} else if (timeDay <= 15000) {
			timeDescription = "evening";
		} else {
			timeDescription = "night";
		}
		
		ChatColor textColour = (isNight ? ChatColor.DARK_RED : ChatColor.YELLOW);
		ChatColor highlightColour = (isNight ? ChatColor.BLUE : ChatColor.AQUA);
		dwarf.sendTitleMessage(textColour + "It is currently " + highlightColour + timeDescription);
	}
}
