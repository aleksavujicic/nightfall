package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 12/03/17.
 */
class Wand extends Bow {
	Wand(Dwarf dwarf) {
		super(dwarf, BowType.WAND);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace face) {
		dwarf.sendMessage(ChatColor.DARK_PURPLE + "WORMHOLE!");
	}
}
