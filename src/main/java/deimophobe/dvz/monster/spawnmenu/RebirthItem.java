package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.menu.SimpleItem;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 16/06/17.
 */
public class RebirthItem implements MenuItem<MonsterPlayer> {
	private final ItemStack item;
	
	public RebirthItem(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		if (monster.canRebirth())
			return item;
		else
			return null;
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		if (!DoomManager.getManager().isDoom()) {
			if (monster.canRebirth()) {
				monster.rebirth();
				session.closeSession();
			} else {
				
			}
		} else {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
		}
		return false;
	}
}
