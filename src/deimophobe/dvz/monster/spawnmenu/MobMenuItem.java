package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



/**
 * Created by Deimophobe on 2/02/17.
 */
abstract class MobMenuItem implements MenuItem {
	private final ItemStack item;
	@Override
	public ItemStack getDisplayItem() {return item;}
	
	private final int xpCost;
	
	MobMenuItem(ItemStack item, int xpCost) {
		this.item = item;
		this.xpCost = xpCost;
	}
	
	@Override
	public final boolean select(Player player) {
		MonsterPlayer monster = MobManager.getManager().getMob(player);
		
		if (monster == null) {
			Bukkit.getLogger().warning("Player " + player.getName() + " used mob menu but was not mob?!");
		}
		
		if (monster.useXP(xpCost)) {
			return onSelect(monster);
		} else {
			monster.sendMessage(ChatColor.RED + "You need at least " + ChatColor.GREEN + xpCost + ChatColor.RED + " experience for that.");
			return false;
		}
	}
	
	abstract boolean onSelect(MonsterPlayer monster);
}
