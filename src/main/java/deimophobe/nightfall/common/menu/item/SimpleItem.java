package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.Misc;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/05/17.
 */
public abstract class SimpleItem<T extends SessionData> implements MenuItem<T> {
	private final ItemStack item;
	
	public SimpleItem(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<T> session) {
		return item;
	}
}
