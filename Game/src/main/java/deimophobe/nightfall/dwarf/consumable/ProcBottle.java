package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 9/01/18.
 */
public class ProcBottle extends Consumable {
	
	protected ProcBottle(String itemName) {
		super(itemName);
	}
	
	@Override
	public int use(Dwarf dwarf, ClickType click, Block clickedBlock, BlockFace face) {
		if (!checkPhase(dwarf)) return FAILED_CD;
		if (click.isRightClick()) return FAILED_CD;
		
		dwarf.giveProc(ProcType.PROC_BOTTLE);
		dwarf.playSound("block.glass.break", 1f, 1f, true);
		dwarf.getWorld().spawnParticle(Particle.ITEM_CRACK, dwarf.getEyeLocation(), 20, 0.5, 0.5, 0.5, 0.05, getItemStack());
		return DEFAULT_CD;
	}
}
