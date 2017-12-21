package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
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
		if (!checkPhase(dwarf)) return FAILED_CD;
		if (!dwarf.getArmour().canPickRepair()) {
			dwarf.sendTitleMessage(ChatColor.GOLD + "Your armour is nearly full!");
			return FAILED_CD;
		}
		
		boolean success = GameMap.getCurrentMap().tryUseGold(60);
		if (success) {
			dwarf.getArmour().repair(2000);
			dwarf.playSound("block.anvil.use", 20, 0.8f, false);
			return DEFAULT_CD;
		} else {
			dwarf.sendTitleMessage(ChatColor.YELLOW + "Not enough gold in the shrine!");
			return FAILED_CD;
		}
	}
}
