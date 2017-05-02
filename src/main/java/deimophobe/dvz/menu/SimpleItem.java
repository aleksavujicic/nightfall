package deimophobe.dvz.menu;

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
