package deimophobe.dvz.dwarf.kit;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GameEntity;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitItemElement extends KitElement {
	void onUse(Action action, Block clickedBlock, BlockFace blockFace);
	void onBlockBreak(Block block);
	
	ItemStack getItem();
	boolean matchesItem(ItemStack toMatch);
	boolean isHoldingItem();
	
	KitGiveType getGiveType();
}
