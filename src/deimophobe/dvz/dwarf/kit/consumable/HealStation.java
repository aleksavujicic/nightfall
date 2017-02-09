package deimophobe.dvz.dwarf.kit.consumable;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.blocks.timedblock.HealBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 28/01/17.
 */
public class HealStation extends Consumable {
	public HealStation(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean use(Dwarf dwarf) {
		Location loc = dwarf.getPlayer().getTargetBlock((Set<Material>) null, 5).getLocation();
		return TimedBlock.placeTimedBlock(new HealBlock(loc, 30*20));
	}
}
