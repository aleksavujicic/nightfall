package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 20/01/17.
 */
class AxeOfMalice extends Sword {
	
	AxeOfMalice(Dwarf dwarf) {
		super(dwarf, SwordType.AXE_OF_MALICE, 1200);
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType b) {
		reduceCooldown(20);
	}
	
	@Override
	public void onUse(Action action, Block clickedBlock, BlockFace blockFace) {
		if (Misc.isRightClick(action) && isOffCD()) {
			dwarf.giveProc(Dwarf.ProcType.MALICE);
			resetCooldown();
		}
	}
	
	@Override
	protected void playOffCDSound() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
}
