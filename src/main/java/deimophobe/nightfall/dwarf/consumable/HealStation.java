package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.blocks.timedblock.HealBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealStation extends Consumable {
	public HealStation(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		if (dwarf.hasPlacedHealBlock()) {
			dwarf.sendTitleMessage(ChatColor.DARK_PURPLE + "You have already placed a heal station!");
			return FAILED_CD;
		}
		
		if (clickedBlock == null) return FAILED_CD;
		
		HealBlock healBlock = new HealBlock(clickedBlock, 30*20, dwarf);
		boolean success =  TimedBlock.placeTimedBlock(healBlock);
		
		if (success) {
			dwarf.setPlacedHealBlock(healBlock);
			return DEFAULT_CD;
		} else {
			return FAILED_CD;
		}
	}
}
