package deimophobe.nightfall.dwarf.kit.elements;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.dwarf.kit.KitGiveType;
import deimophobe.nightfall.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/03/17.
 */
class DwarfAxe extends AbstractItem {
	
	public DwarfAxe(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("misc", "axe");
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.AXE; }
	
	
	@Override
	public void onBlockBreak(Block block, boolean didBreak) {
		if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
			dwarf.giveConsumable(ConsumableType.LOG, 2);
		}
	}
}
