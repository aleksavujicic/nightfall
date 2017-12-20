package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitItemElement extends KitElement {
	boolean onUse(Action action, Block clickedBlock, BlockFace blockFace);
	void onBlockBreak(Block block, boolean didBreak);
	
	CustomItem getItem();
	boolean matchesItem(ItemStack toMatch);
	boolean isHoldingItem();
	
	KitGiveType getGiveType();
}
