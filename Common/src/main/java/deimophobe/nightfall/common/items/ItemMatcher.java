package deimophobe.nightfall.common.items;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 7/05/18.
 */
@FunctionalInterface
public interface ItemMatcher {
	boolean doesItemMatch(ItemStack item);
}
