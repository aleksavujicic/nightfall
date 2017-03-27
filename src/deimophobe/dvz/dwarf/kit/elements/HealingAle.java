package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 22/01/17.
 */
class HealingAle extends AbstractAle {
	
	HealingAle(Dwarf dwarf) {
		super(dwarf, 100);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("ale.healing", Slot.MAIN_HAND);
	@Override public ItemStack getItem() { return ITEM; }
}
