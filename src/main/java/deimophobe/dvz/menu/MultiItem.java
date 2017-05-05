package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MultiItem<T extends SessionData> implements MenuItem<T> {
	private final Collection<MenuItem<T>> items = new HashSet<>();
	
	public void addItem(MenuItem<T> item) {
		items.add(item);
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<T> session) {
		for (MenuItem<T> item : items) {
			ItemStack stack = item.getDisplayItem(session);
			if (stack != null)
				return stack;
		}
		return null;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		for (MenuItem<T> item : items) {
			if (item.getDisplayItem(session) != null)
				return item.onClick(session);
		}
		return false;
	}
}
