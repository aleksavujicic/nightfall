package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 22/01/17.
 */
class JimmyJuice extends Ale {
	JimmyJuice(Dwarf dwarf) {
		super(dwarf, AleType.JIMMYJUICE, 120, true);
	}
	
	@Override
	protected boolean ability(Action type) {
		if (isRightClick(type)) return false;
		if (!useMana()) return false;
		
		dwarf.healPlayerMax();
		dwarf.playSound("entity.generic.drink", 0.6f, 0.9f, false);
		dwarf.playSound("entity.experience_orb.pickup", 1f, 1f, false);
		
		
		return true;
	}
}
