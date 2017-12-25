package deimophobe.nightfall.common.menu.submenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.menu.item.MenuItem;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Deimophobe on 25/12/17.
 */
public class ListMenu<T extends SessionData> implements SubMenu<T> {
	private final List<MenuItem<T>> menuItems = new ArrayList<>();
	
	@Override
	public int getSize() {
		int size = menuItems.size();
		int mod = size % 9;
		
		if (mod == 0) return size;
		else return size + 9 - mod;
	}
	
	@Override
	public Map<Integer, ItemStack> getItems(MenuSession<T> session) {
		Map<Integer, ItemStack> allItems = new HashMap<>();
		int i = 0;
		for (MenuItem<T> item : menuItems) {
			ItemStack stack = item.getDisplayItem(session);
			if (stack != null)
				allItems.put(i, stack);
			i++;
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
	
	public void addItem(int i, MenuItem<T> item) {
		if (item == null) throw new NullPointerException("Cannot place null item in menu.");
		menuItems.add(i, item);
	}
	
	public void addItem(MenuItem<T> item) {
		if (item == null) throw new NullPointerException("Cannot place null item in menu.");
		menuItems.add(item);
	}
	
	public MenuItem getItem(int i) {
		return menuItems.get(i);
	}
	
	public Collection<MenuItem<T>> getMenuItems() {
		return menuItems;
	}
}
