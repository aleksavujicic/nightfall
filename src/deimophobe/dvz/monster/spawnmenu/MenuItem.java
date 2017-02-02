package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * For anything that can be selected in the
 * mob menu - eg. A spawn egg or upgrade menu.
 * Created by Deimophobe on 2/02/17.
 */
abstract class MenuItem {
	private final ItemStack item;
	ItemStack getDisplayItem() {return item;}
	
	private final int xpCost;
	
	MenuItem(ItemStack item, int xpCost) {
		this.item = item;
		this.xpCost = xpCost;
	}
	
	/**
	 * Called when a monster clicks on a menu item
	 *
	 * @param monster The monster that clicked the item
	 * @return Whether the item menu should close or not
	 */
	final boolean select(MonsterPlayer monster) {
		if (monster.useXP(xpCost)) {
			return onSelect(monster);
		} else {
			monster.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GREEN + xpCost + ChatColor.RED + " experience for that.");
			return false;
		}
	}
	
	abstract boolean onSelect(MonsterPlayer monster);
	abstract boolean isAvailable();
}
