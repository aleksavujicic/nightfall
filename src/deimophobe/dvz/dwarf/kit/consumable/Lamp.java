package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.timedblock.LampBlock;
import deimophobe.dvz.timedblock.TimedBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 22/01/17.
 */
class Lamp extends Consumable {
	
	Lamp(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		Location lampLoc = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5).getLocation();
		return TimedBlock.placeTimedBlock(new LampBlock(lampLoc, 60*20));
	}
}
