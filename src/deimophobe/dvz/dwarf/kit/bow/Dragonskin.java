package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.monster.PlayerMonster;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends Bow {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf, BowType.DRAGONSKIN, 30);
	}
	
	@Override
	public void onKill(PlayerMonster monster) {
		dwarf.giveProc(Dwarf.ProcType.DRAGONSKIN);
	}
}
