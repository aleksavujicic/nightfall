package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.blocks.timedblock.LampBlock;
import deimophobe.nightfall.blocks.timedblock.TimedBlock;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.items.CustomItem;
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
		//if (Misc.getArrowForce(arrow) == 1) TODO
			TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20, dwarf));
	}
}
