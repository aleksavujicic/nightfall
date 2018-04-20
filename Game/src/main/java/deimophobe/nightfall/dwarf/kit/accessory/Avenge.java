package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Avenge extends AbstractPiece {
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
