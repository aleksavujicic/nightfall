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
	public ConsumeResult use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		ConsumeResult phaseCheck = checkPhase();
		if (phaseCheck != null) return phaseCheck;
		if (face == null) return ConsumeResult.FAILURE;
		
		TurretBlock turret = new TurretBlock(45, clickedBlock, dwarf, face, 70);
		boolean success = BlockManager.getManager().placeTimedBlock(turret);
		
		if (success) return ConsumeResult.SUCCESS;
		else return ConsumeResult.FAILURE;
	}
}
