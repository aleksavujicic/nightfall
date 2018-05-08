package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Wrench extends Consumable {
	
	Wrench(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (click.isRightClick()) return FAILED_CD;
		if (!checkPhase(dwarf)) return FAILED_CD;
		if (!dwarf.getArmour().canPickRepair()) {
			dwarf.sendTitleMessage(ChatColor.GOLD + "Your armour is nearly full!");
			return FAILED_CD;
		}
		
		boolean success = GameMap.getCurrentMap().useGold(75);
		dwarf.getArmour().repair( success ? 1000 : 250 );
		dwarf.playSound("block.anvil.use", 20, 0.8f, false);
		return DEFAULT_CD;
	}
}
