package deimophobe.nightfall.menu;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MenuListener implements Listener {
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		// If clicker is player
		HumanEntity clicker = event.getWhoClicked();
		if (clicker instanceof Player) {
			// And has active session
			MenuSession session = MenuManager.getManager().getSession((Player) clicker);
			if (session != null) {
				// If it was a regular click and on inventory screen
				if (event.getClick() == ClickType.LEFT && event.getClickedInventory() != null && event.getClickedInventory().getHolder() == null)
					session.onClick(event.getSlot());
				
				// And cancel regardless
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		// If clicker is player
		HumanEntity clicker = event.getPlayer();
		if (clicker instanceof Player) {
			// Close active session (if there is any)
			MenuManager.getManager().closeSession((Player) clicker);
		}
	}
}
