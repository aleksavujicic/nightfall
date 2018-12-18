package deimophobe.nightfall.dwarf.kit;

import deimophobe.nightfall.ClickType;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface ItemPiece extends KitPiece, ItemMatcher {
	boolean onUse(ClickType click, Block clickedBlock, BlockFace blockFace);
	void onBlockBreak(Block block, boolean didBreak);
	
	CustomItem getItem();
	boolean isHoldingItem();
	
	PickupType getGiveType();
}
