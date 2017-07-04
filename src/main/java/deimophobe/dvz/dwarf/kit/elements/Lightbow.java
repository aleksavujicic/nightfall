package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Lightbow extends AbstractBow {
	Lightbow(Dwarf dwarf) {
		super(dwarf);
		dwarf.makeBlindImmune();
	}
	
	private final static int POWER = 30;
	private final static CustomItem ITEM = DwarvenItems.getBow("lightbow", POWER);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public String getBowIdentifier() {return "LIGHTBOW";}
	@Override public int getPower() {return POWER;}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		if (Misc.getArrowForce(arrow) == 1)
			TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20, dwarf));
	}
}
