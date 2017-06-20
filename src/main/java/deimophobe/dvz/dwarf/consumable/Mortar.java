package deimophobe.dvz.dwarf.consumable;

import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.BlockConverter;
import deimophobe.dvz.blocks.BlockManager;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Mortar extends Consumable {
	private final boolean wizzy;
	
	Mortar(String item, boolean wizzy) {
		super(item);
		this.wizzy = wizzy;
	}
	
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (Misc.isRightClick(action)) return FAILED_CD;
		
		Block block = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5);
		BlockConverter.convert(BlockConverter.Type.MORTAR, block.getLocation(), 5);
		
		return DEFAULT_CD;
		/*
		boolean used = BlockManager.getManager().mortarWalls(block, wizzy);
		
		if (used) {
			dwarf.playSound("mortar", 1, (float) (0.6 + 0.1 * Math.random() + (wizzy ? 0.2 : 0)), false);
			return DEFAULT_CD;
		} else {
			return FAILED_CD;
		}
		*/
	}
}
