package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.blocks.BlockManager;
import deimophobe.nightfall.blocks.timedblock.TurretBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 26/04/18.
 */
public class Turret extends Consumable {
	protected Turret(String itemName) {
		super(itemName);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		boolean success = BlockManager.getManager().placeTimedBlock(new TurretBlock(30, clickedBlock, dwarf, face, 70));
		
		if (success) return DEFAULT_CD;
		else return FAILED_CD;
	}
}
