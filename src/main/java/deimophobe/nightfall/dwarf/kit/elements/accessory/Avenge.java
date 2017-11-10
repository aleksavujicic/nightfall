package deimophobe.nightfall.dwarf.kit.elements.accessory;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.elements.AbstractElement;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Avenge extends AbstractElement {
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
