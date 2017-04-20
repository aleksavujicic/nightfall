package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MenuItem<T> {
	/**
	 * Shows the item that will be displayed in the menu
	 * @return The item to display.
	 * @param player Player to display to.
	 */
	ItemStack getDisplayItem(T player);
	
	/**
	 * Called when a player clicks on a menu item
	 *
	 * @param player The player that clicked the item
	 * @return Whether the menu should be refreshed
	 */
	boolean select(T player);
	
	/**
	 * Whether or not the item should be visible
	 * @param player The player who is viewing the menu of the item
	 * @return true if the player should see the item, false if they should not.
	 */
	boolean isAvailable(T player);
}
