package deimophobe.nightfall.common.items.base;

import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/04/17.
 */
public interface BaseItem extends Cloneable, ItemMatcher {
	ItemStack createItem();
	boolean isSimilar(BaseItem item);
}
