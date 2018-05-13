package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
class DoomClockItem implements MenuItem<MonsterPlayer> {
	
	private final ItemStack item;
	
	private final int xpCost;
	private final int time;
	
	DoomClockItem(ItemStack item, int xpCost, int time) {
		this.item = item;
		
		this.xpCost = xpCost;
		this.time = time;
	}
	
	private boolean isAvailable() {
		return (!DoomManager.getManager().isDoom());
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		return item;
	}
	
	@Override
	public final boolean onClick(MenuSession<MonsterPlayer> session) {
		if (!isAvailable()) return false;
		
		MonsterPlayer monster = session.getData();
		if (monster.useExp(xpCost)) {
			DoomManager.getManager().reduceDoom(time);
		} else {
			monster.sendMessage(ChatColor.RED + "Not enough exp! " + "You have " + ChatColor.AQUA + monster.getExp() +
					ChatColor.RED + "/" + ChatColor.GREEN + xpCost);
		}
		return false;
	}
}
