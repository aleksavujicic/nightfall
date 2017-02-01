package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;

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
	protected boolean ability() {
		dwarf.giveProc(Dwarf.ProcType.MALICE);
		return true;
	}
	
	@Override
	protected void playOffCDSound() {
		dwarf.playSound("entity.elder_guardian.curse", 1, 1f, false);
	}
}
