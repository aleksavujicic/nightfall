package deimophobe.dvz.menu;

import com.comphenix.protocol.PacketType;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class Menu {
	private final Map<Integer, MenuItem> menuItems = new HashMap<>();
	private final String title;
	private final int rows;
	
	
	private final InventoryHolder menuHolder = new InventoryHolder() {
		@Override
		public Inventory getInventory() {
			Inventory guiInventory = Bukkit.createInventory(menuHolder, rows*9, title);
			
			for (Map.Entry<Integer, MenuItem> entry : menuItems.entrySet()) {
				int index = entry.getKey();
				MenuItem item = entry.getValue();
				if (!item.isAvailable()) continue;
				
				guiInventory.setItem(index, item.getDisplayItem());
			}
			
			return guiInventory;
		}
	};
	
	public void showTo(Player player) {
		player.openInventory(menuHolder.getInventory());
	}
	
	public boolean isInventory(Inventory inv) {
		return (inv != null && inv.getHolder() == menuHolder);
	}
	
	
	public Menu(String title, int rows) {
		this.title = title;
		this.rows = rows;
	}
	
	
	public boolean select(int i, Player player) {
		MenuItem item = menuItems.get(i);
		if (item != null && item.isAvailable())
			return item.select(player);
		return false;
	}
	
	protected void addItem(int i, MenuItem item) {
		menuItems.put(i, item);
	}
	
	protected Collection<MenuItem> getItems() {
		return menuItems.values();
	}
}
