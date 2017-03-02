package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Wrench extends Consumable {
	
	Wrench(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		boolean success = ShrineManager.getManager().useGold(15);
		if (success) {
			dwarf.repairArmour(10000);
			dwarf.playSound("block.anvil.use", 20, 0.8f, false);
		}
		return success;
	}
}
