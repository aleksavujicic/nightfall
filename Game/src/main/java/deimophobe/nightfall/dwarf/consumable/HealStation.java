package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.HealBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealStation extends Consumable {
	public HealStation(String item) {
		super(item);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		// Check no other nearby heal blocks
		Location blockLoc = clickedBlock.getLocation();
		for (HealBlock healBlock : BlockManager.getManager().getTimedBlocks(HealBlock.class)) {
			Location healLoc = healBlock.getBlock().getLocation();
			if (blockLoc.distance(healLoc) < 12) {
				dwarf.sendTitleMessage(ChatColor.RED + "Too close to another heal station");
				return FAILED_CD;
			}
		}
		
		// Place it
		HealBlock healBlock = new HealBlock(clickedBlock, 20*20, dwarf);
		boolean success =  BlockManager.getManager().placeTimedBlock(healBlock);
		
		return (success ? DEFAULT_CD : FAILED_CD);
	}
}
