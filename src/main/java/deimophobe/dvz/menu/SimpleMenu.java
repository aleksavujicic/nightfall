package deimophobe.dvz.menu;

import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class SimpleMenu<T extends SessionData> implements SubMenu<T> {
	private final Map<Integer, MenuItem<T>> menuItems = new HashMap<>();
	private final int size;
	
	public SimpleMenu(int size) {
		this.size = size;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(MenuSession<T> session) {
		Map<Integer, ItemStack> allItems = new HashMap<>();
		for (Map.Entry<Integer, MenuItem<T>> entry : menuItems.entrySet()) {
			int index = entry.getKey();
			MenuItem<T> item = entry.getValue();
			ItemStack stack = item.getDisplayItem(session);
			if (stack != null)
				allItems.put(index, stack);
		}
		return allItems;
	}
	
	@Override
	public boolean onClick(int i, MenuSession<T> session) {
		MenuItem<T> item = menuItems.get(i);
		if (item == null) return false;
		
		return item.onClick(session);
	}
	
	@Override
	public void onClose(MenuSession<T> session) {}
	
	public void setItem(int i, MenuItem<T> item) {
		menuItems.put(i, item);
	}
}
