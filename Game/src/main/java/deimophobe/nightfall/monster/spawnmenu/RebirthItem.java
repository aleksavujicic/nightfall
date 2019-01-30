package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.SpawnMethod;
import deimophobe.nightfall.monster.doom.DoomManager;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 16/06/17.
 */
class RebirthItem implements MenuItem<MonsterPlayer> {
	private final ItemStack item;
	
	RebirthItem(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		if (monster.canRebirth()) {
			return item;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<MonsterPlayer> session) {
		MonsterPlayer monster = session.getData();
		if (!DoomManager.getManager().isDoom()) {
			if (monster.canRebirth()) {
				monster.spawnPrimaryMob(SpawnMethod.REBIRTH);
				session.closeSession();
			} else {
				//monster.sendMessage(ChatColor.RED + "You can no longer rebirth!");
				return true;
			}
		} else {
			monster.sendMessage(ChatColor.RED + "You cannot spawn during doom!");
		}
		return false;
	}
}
