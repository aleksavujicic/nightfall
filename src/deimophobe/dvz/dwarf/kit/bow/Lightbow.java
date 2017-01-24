package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.timedblock.LampBlock;
import deimophobe.dvz.timedblock.TimedBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Lightbow extends Bow {
	Lightbow(Dwarf dwarf) {
		super(dwarf, BowType.LIGHTBOW, 50);
	}
	
	@Override
	public void onArrowLand(Arrow arrow, Block hitBlock) {
		if (hitBlock != null) {
			TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20));
		}
	}
}
