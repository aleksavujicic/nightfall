package deimophobe.dvz.menu;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MenuItem {
	ItemStack getDisplayItem();
	
	/**
	 * Called when a player clicks on a menu item
	 *
	 * @param player The player that clicked the item
	 * @return Whether the item menu should close or not
	 */
	boolean select(Player player);
	boolean isAvailable();
}
