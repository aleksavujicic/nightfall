package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Deimophobe on 3/02/17.
 */
class BackMenuItem implements MenuItem<MonsterPlayer> {
	private static final ItemStack item;
	static {
		item = new ItemStack(Material.DIAMOND);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Back");
		item.setItemMeta(meta);
	}
	@Override
	public ItemStack getDisplayItem() {
		return item;
	}
	
	private final static BackMenuItem menuItem = new BackMenuItem();
	static BackMenuItem getBackMenuItem() {return menuItem;}
	
	private BackMenuItem() {}
	
	
	@Override
	public boolean select(MonsterPlayer monster) {
		monster.showMobMenu();
		return false;
	}
	
	@Override
	public boolean isAvailable(MonsterPlayer monsterPlayer) {
		return true;
	}
}
