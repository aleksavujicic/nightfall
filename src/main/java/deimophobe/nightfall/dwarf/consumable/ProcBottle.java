package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 9/01/18.
 */
public class ProcBottle extends Consumable {
	
	protected ProcBottle(String itemName) {
		super(itemName);
	}
	
	@Override
	public int use(Dwarf dwarf, Action action, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		
		dwarf.giveProc(ProcType.PROC_BOTTLE);
		dwarf.playSound("block.glass.break", 1f, 1f, true);
		return DEFAULT_CD;
	}
}
