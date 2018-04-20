package deimophobe.nightfall.common.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MenuSession<T extends SessionData> {
	
	private final MainMenu<T> mainMenu;
	private final Player player;
	
	private final T data;
	
	private final Inventory inventory;
	
	
	MenuSession(MainMenu<T> mainMenu, Player player) {
		this.mainMenu = mainMenu;
		this.player = player;
		
		this.data = mainMenu.getDataFromPlayer(player);
		
		int mainMenuSize = mainMenu.getSize();
		int leftover = mainMenuSize % 9;
		int rows = mainMenuSize/9;
		if (leftover != 0) rows++;
		
		this.inventory = Bukkit.createInventory(null, rows*9, mainMenu.getTitle());
		updateInventory();
		player.openInventory(inventory);
	}
	
	public Player getPlayer() {
		return player;
	}
	public T getData() {
		return data;
	}
	public Inventory getInventory() { return inventory; }
	
	
	void onClick(int index) {
		boolean update = mainMenu.onClick(index, this);
		if (update)
			updateInventory();
	}
	
	private void updateInventory() {
		Map<Integer, ItemStack> items = mainMenu.getItems(this);
		inventory.clear();
		for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue());
		}
	}
	
	public void closeSession() {
		player.closeInventory();
	}
	
	void onClose() {
		player.closeInventory();
		mainMenu.onClose(this);
	}
}
