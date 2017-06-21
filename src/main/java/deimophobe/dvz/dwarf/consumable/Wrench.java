package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Wrench extends Consumable {
	
	Wrench(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		
		switch (Game.getGame().getPhase()) {
			case STARTING:
			case BUILD:
			case PLAGUE:
				dwarf.getPlayer().sendTitle("", ChatColor.YELLOW + "You cannot use wrenches until the monsters are released.", 5, 30, 5);
				return FAILED_CD;
			case END:
				dwarf.getPlayer().sendTitle("", ChatColor.RED + "You cannot use wrenches after the game is over.", 5, 30, 5);
				return FAILED_CD;
		}
		
		boolean success = ShrineManager.getManager().useGold(60);
		if (success) {
			dwarf.getArmour().repair(1000);
			dwarf.playSound("block.anvil.use", 20, 0.8f, false);
			return DEFAULT_CD;
		} else {
			dwarf.sendMessage(ChatColor.YELLOW + "Not enough gold in the shrine!");
			return FAILED_CD;
		}
	}
}
