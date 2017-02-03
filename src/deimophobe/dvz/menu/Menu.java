package deimophobe.dvz.menu;

import com.comphenix.protocol.PacketType;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.*;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class Menu<T extends GamePlayer> {
	private final Map<Integer, Set<MenuItem<T>>> menuItems = new HashMap<>();
	private final String title;
	private final int rows;
	
	
	private final InventoryHolder menuHolder = () -> null;
	
	public boolean isInventory(Inventory inv) {
		return ((inv != null) && (inv.getHolder() == menuHolder));
	}
	
	
	public Menu(String title, int rows) {
		this.title = title;
		this.rows = rows;
		menus.put(title, this);
	}
	
	public void showTo(T player) {
		Inventory guiInventory = Bukkit.createInventory(menuHolder, rows*9, title);
		
		for (Map.Entry<Integer, Set<MenuItem<T>>> entry : menuItems.entrySet()) {
			int index = entry.getKey();
			Set<MenuItem<T>> items = entry.getValue();
			for (MenuItem<T> item : items) {
				if (item.isAvailable(player)) {
					guiInventory.setItem(index, item.getDisplayItem());
					break;
				}
			}
		}
		
		player.showInventory(guiInventory);
	}
	
	
	public boolean select(int i, T player) {
		Set<MenuItem<T>> items = menuItems.get(i);
		if (items == null) return false;
		
		for (MenuItem<T> item : items) {
			if (item != null && item.isAvailable(player))
				return item.select(player);
		}
		return false;
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
		if (menuItems.get(i) == null)
			menuItems.put(i, new HashSet<>());
	}
	
	protected Collection<MenuItem<T>> getItems() {
		Collection<MenuItem<T>> allItems = new HashSet<>();
		for (Set<MenuItem<T>> items : menuItems.values()) {
			allItems.addAll(items);
		}
		return allItems;
	}
	
	
	private static final Map<String, Menu> menus = new HashMap<>();
	public static Menu getMenuFromInv(Inventory inv) {
		if (inv == null) return null;
		return menus.get(inv.getTitle());
	}
}
