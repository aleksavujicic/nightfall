package deimophobe.nightfall.common.menu;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MenuSession<T extends SessionData> {
	
	private final MainMenu<T> mainMenu;
	private final Player player;
	
	private T data;
	
	private final Inventory inventory;
	
	private final MenuSession<?> previousSession;
	
	private boolean dormant = false;
	
	
	MenuSession(MainMenu<T> mainMenu, Player player, MenuSession<?> previousSession) {
		this.mainMenu = mainMenu;
		this.player = player;
		this.previousSession = previousSession;
		
		this.data = mainMenu.getDataFromPlayer(player);
		
		int mainMenuSize = mainMenu.getSize();
		int leftover = mainMenuSize % 9;
		int rows = mainMenuSize/9;
		if (leftover != 0) rows++;
		
		this.inventory = Bukkit.createInventory(null, rows*9, mainMenu.getTitle());
		updateInventory();
	}
	
	public Player getPlayer() {
		return player;
	}
	public T getData() {
		return data;
	}
	MainMenu<T> getMenu() {
		return mainMenu;
	}
	
	private void reloadData() {
		this.data = mainMenu.getDataFromPlayer(player);
	}
	
	void onClick(int index) {
		checkActiveSession();
		
		boolean update = mainMenu.onClick(index, this);
		if (update) updateInventory();
	}
	
	private void updateInventory() {
		checkActiveSession();
		
		Map<Integer, ItemStack> items = mainMenu.getItems(this);
		inventory.clear();
		for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
			inventory.setItem(entry.getKey(), entry.getValue());
		}
		
		if (player.getOpenInventory().getType() == InventoryType.CRAFTING) {
			player.openInventory(inventory);
		}
	}
	
	public void openNewSession(MainMenu<?> newMenu) {
		checkActiveSession();
		
		dormant = true;
		player.closeInventory();
		MenuSession<?> newSession = new MenuSession<>(newMenu, player, this);
		MenuManager.getManager().setSession(newSession);
	}
	
	private void resume() {
		checkState(dormant, "Can only resume a dormant session: %s.", this);
		
		dormant = false;
		reloadData();
		updateInventory();
	}
	
	public void closeSession() {
		checkActiveSession();
		
		player.closeInventory();
	}
	
	void onClose() {
		if (dormant) return;
		mainMenu.onClose(this);
		
		if (previousSession != null) {
			player.closeInventory();
			
			previousSession.resume();
			MenuManager.getManager().setSession(previousSession);
			new BukkitRunnable() {
				@Override
				public void run() {
					previousSession.updateInventory();
				}
			}.runTask(NightfallCommonPlugin.getPlugin());
		}
	}
	
	
	
	boolean isMenu(MainMenu<?> menu) {
		return mainMenu == menu;
	}
	
	private void checkActiveSession() {
		checkState(!dormant, "Cannot edit dormant session: %s.", this);
	}
	
	@Override
	public String toString() {
		String menuName = mainMenu.getClass().getSimpleName();
		String playerName = player.getName();
		return "[" + playerName + "; " + menuName + "; " + this.hashCode() + "]";
	}
}
