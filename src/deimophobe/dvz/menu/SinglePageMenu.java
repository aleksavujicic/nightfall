package deimophobe.dvz.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.*;

/**
 * Created by Deimophobe on 2/03/17.
 */
public abstract class SinglePageMenu<T> implements Menu<T> {
	private final Map<Integer, Set<MenuItem<T>>> menuItems = new HashMap<>();
	private final String title;
	private final int rows;
	
	public SinglePageMenu(String title, int rows) {
		this.title = title;
		this.rows = rows;
		GlobalMenuList.registerMenu(this);
	}
	
	@Override
	public Inventory getInventory(T player) {
		Inventory guiInventory = Bukkit.createInventory(null, rows*9, title);
		
		for (Map.Entry<Integer, Set<MenuItem<T>>> entry : menuItems.entrySet()) {
			int index = entry.getKey();
			Set<MenuItem<T>> items = entry.getValue();
			for (MenuItem<T> item : items) {
				if (item.isAvailable(player)) {
					guiInventory.setItem(index, item.getDisplayItem(player));
					break;
				}
			}
		}
		return guiInventory;
	}
	
	@Override
	public void select(int i, T player) {
		Set<MenuItem<T>> items = menuItems.get(i);
		if (items == null) return;
		
		for (MenuItem<T> item : items) {
			if (item != null && item.isAvailable(player)) {
				boolean refresh =  item.select(player);
				
				if (refresh)
					showTo(player);
			}
		}
	}
	
	
	@Override
	public String getTitle() {
		return title;
	}
	
	
	protected void addItem(int i, MenuItem<T> item) {
		checkNull(i);
		menuItems.get(i).add(item);
	}
	
	protected void setItem(int i, MenuItem<T> item) {
		menuItems.put(i, Collections.singleton(item));
	}
	
	protected void removeAllItems(int i) {
		menuItems.remove(i);
	}
	
	protected void removeItem(int i, MenuItem<T> item) {
		menuItems.get(i).remove(item);
	}
	
	private void checkNull(int i) {
		menuItems.computeIfAbsent(i, k -> new HashSet<>());
	}
	
	protected Collection<MenuItem<T>> getItems() {
		Collection<MenuItem<T>> allItems = new HashSet<>();
		for (Set<MenuItem<T>> items : menuItems.values()) {
			allItems.addAll(items);
		}
		return allItems;
	}
}
