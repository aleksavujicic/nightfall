package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HealingAle extends Ale {
	
	HealingAle(Dwarf dwarf) {
		super(dwarf, AleType.HEALING);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (!isOffCD()) return;
		if (Misc.isRightClick(action)) return;
		if (!dwarf.tryUseMana(100)) return;
		
		dwarf.healMax();
		dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
		
		resetCooldown();
	}
}
