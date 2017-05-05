package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.ProcType;

/**
 * Created by Deimophobe on 27/03/17.
 */
class Avenge extends AbstractElement {
	public Avenge(Dwarf dwarf) {
		super(dwarf);
	}
	
	@Override
	public void notifyDeath(Dwarf dead) {
		if (dwarf != dead && Game.getGame().getPhase() == Phase.GAME) {
			dwarf.playSound("horn", 100, 1, false);
			dwarf.giveProc(ProcType.AVENGE);
		}
	}
}
