package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import deimophobe.dvz.dwarf.Dwarf;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Dragonskin extends Bow {
	
	Dragonskin(Dwarf dwarf) {
		super(dwarf, BowType.DRAGONSKIN, 30);
	}
	
	@Override
	public void onKill(GameEntity monster, DamageType type) {
		if (type.isRanged())
			dwarf.giveProc(Dwarf.ProcType.DRAGONSKIN);
	}
}
