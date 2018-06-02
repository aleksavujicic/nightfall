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
	
	private final MainMenu<?> previousMenu;
	
	
	MenuSession(MainMenu<T> mainMenu, Player player, MainMenu<?> previousMenu) {
		this.mainMenu = mainMenu;
		this.player = player;
		this.previousMenu = previousMenu;
		
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
	
	void onClick(int index) {
		boolean update = mainMenu.onClick(index, this);
		if (update) updateInventory();
	}
	
	private void updateInventory() {
		Map<Integer, ItemStack> items = mainMenu.getItems(this);
		inventory.clear();
		for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue());
		}
	}
	
	public void openNewSession(MainMenu<?> newMenu) {
		closeSession();
		
		MenuSession<?> newSession = new MenuSession<>(newMenu, player, mainMenu);
		MenuManager.getManager().startSession(newSession);
	}
	
	public void closeSession() {
		player.closeInventory();
	}
	
	void onClose() {
		mainMenu.onClose(this);
		
		if (previousMenu != null) {
			MenuManager.getManager().startSession(previousMenu, player);
		}
	}
	
	boolean isMenu(MainMenu<?> menu) {
		return mainMenu == menu;
	}
}
