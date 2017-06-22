package deimophobe.dvz.dwarf.kit.elements;

import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarvenItems;
import deimophobe.dvz.dwarf.kit.KitGiveType;
import deimophobe.dvz.items.CustomItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;

/**
 * Created by Deimophobe on 21/06/17.
 */
public class Chestomatic extends AbstractItem {
	
	public Chestomatic(Dwarf dwarf) {
		super(dwarf);
	}
	
	private final static CustomItem ITEM = DwarvenItems.getItem("misc.chesto", Slot.MAIN_HAND);
	@Override public CustomItem getItem() {
		return ITEM;
	}
	@Override public KitGiveType getGiveType() { return KitGiveType.START; }
	
	@Override
	public boolean onUse(Action action, Block block, BlockFace face) {
		dwarf.showSharedChest();
		return true;
	}
}
