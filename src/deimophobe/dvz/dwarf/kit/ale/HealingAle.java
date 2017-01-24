package deimophobe.dvz.dwarf.kit.ale;

import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HealingAle extends Ale {
	
	HealingAle(Dwarf dwarf) {
		super(dwarf, AleType.HEALING, 100);
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
