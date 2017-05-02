package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;



/**
 * Created by Deimophobe on 2/02/17.
 */
abstract class CostMobMenuItem implements MenuItem<MonsterPlayer> {
	private final ItemStack item;
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> player) {return item;}
	
	private final int xpCost;
	
	CostMobMenuItem(ItemStack item, int xpCost) {
		this.item = item;
		this.xpCost = xpCost;
	}
	
	@Override
	public final boolean select(MonsterPlayer monster) {
		if (monster.useXP(xpCost)) {
			return onPayCost(monster);
		} else {
			monster.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GREEN + xpCost + ChatColor.RED + " experience for that.");
			return false;
		}
	}
	
	protected abstract boolean onPayCost(MonsterPlayer monster);
}
