package deimophobe.nightfall.common.menu.submenu;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Deimophobe on 1/05/17.
 */
public interface SubMenu<T extends SessionData> {
	int getSize();
	/**
	 * Gets a list of all items that should be displayed.
	 * @param session The current active menu session.
	 * @return List of items that need to be displayed.
	 */
	Map<Integer, ItemStack> getItems(MenuSession<T> session);
	
	/**
	 *
	 * @param i Index of click.
	 * @param session The current active menu session.
	 * @return Whether the menu need to be updated.
	 */
	boolean onClick(int i, MenuSession<T> session);
	
	/**
	 *
	 * @param session The session being closed.
	 * @return Whether the menu need to be updated.
	 */
	void onClose(MenuSession<T> session);
}
