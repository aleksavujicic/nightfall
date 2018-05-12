package deimophobe.nightfall.common.items;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 7/05/18.
 */
@FunctionalInterface
public interface ItemMatcher {
	boolean doesItemMatch(@NotNull ItemStack item);
}
