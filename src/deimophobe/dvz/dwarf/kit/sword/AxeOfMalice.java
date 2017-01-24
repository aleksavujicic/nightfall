package deimophobe.dvz.dwarf.kit.sword;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;

/**
 * Created by Deimophobe on 20/01/17.
 */
class AxeOfMalice extends Sword {
	
	AxeOfMalice(Dwarf dwarf) {
		super(dwarf, SwordType.AXE_OF_MALICE, 1200);
	}
	
	@Override
	public void onKill(PlayerMonster monster) {
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
