package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfAxe extends AbstractItem {
	
	public DwarfAxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static ItemStack ITEM = DwarfManager.getManager().getItem("misc.axe", Slot.MAIN_HAND);
	@Override public ItemStack getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.AXE; }
	
	
	@Override
	public void onBlockBreak(Block block) {
		if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
			dwarf.giveConsumable(ConsumableType.LOG, 2);
		}
	}
}
