package deimophobe.nightfall.game.player;

import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

/**
 * Created by Deimophobe on 22/06/18.
 */
interface InventoryIterator extends Iterator<ItemStack> {
	void replace(ItemStack newItem);
	
	@Override
	default void remove() {
		replace(null);
	}
}
