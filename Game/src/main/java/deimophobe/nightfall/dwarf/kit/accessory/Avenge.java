package deimophobe.nightfall.dwarf.kit.accessory;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.ProcType;
import deimophobe.nightfall.dwarf.armour.Armour;
import deimophobe.nightfall.dwarf.kit.AbstractPiece;
import deimophobe.nightfall.dwarf.kit.ArmourPiece;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;

/**
 * Created by Deimophobe on 27/03/17.
 */
public class Avenge extends AbstractPiece implements ArmourPiece {
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
	
	@Override
	public void onArmourEquip(Armour armour) {
		armour.addModifier(ItemModifierType.AVENGE, 1);
	}
}
