package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.blocks.timedblock.LampBlock;
import deimophobe.dvz.blocks.timedblock.TimedBlock;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/01/17.
 */
class Lightbow extends AbstractBow {
	Lightbow(Dwarf dwarf) {
		super(dwarf);
		dwarf.makeBlindImmune();
	}
	
	private final static ItemStack ITEM = DwarvenItems.getItem("bow.lightbow", Slot.MAIN_HAND);
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.BOW; }
	@Override public String getBowIdentifier() {return "LIGHTBOW";}
	@Override public int getPower() {return 30;}
	
	@Override
	public void onProjectileLand(Projectile arrow, Block hitBlock) {
		if (Misc.getArrowForce(arrow) == 1)
			TimedBlock.placeTimedBlock(new LampBlock(hitBlock, 10*20));
	}
}
