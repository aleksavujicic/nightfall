package deimophobe.dvz.dwarf.kit.bow;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Lightbow extends Bow {
	Lightbow(Dwarf dwarf) {
		super(dwarf, BowType.LIGHTBOW, 30);
	}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20));
	}
}
