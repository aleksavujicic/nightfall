package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MenuItem<T extends SessionData> {
	/**
	 * Shows the item that will be displayed in the menu
	 * @param session Data about how to display.
	 * @return The item to display. Returning null hides the item.
	 */
	ItemStack getDisplayItem(MenuSession<T> session);
	
	/**
	 * Called when a player clicks on a menu item
	 *
	 * @param session Data
	 * @return Whether the menu need to be updated
	 */
	boolean onClick(MenuSession<T> session);
}
