package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.menu.MenuSession;
import deimophobe.dvz.monster.MonsterPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Deimophobe on 3/02/17.
 */
class BackMenuItem implements MenuItem<MonsterPlayer> {
	private static final ItemStack item;
	static {
		item = new ItemStack(Material.DIAMOND_HOE, 1, (short) 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("Back");
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(meta);
	}
	@Override
	public ItemStack getDisplayItem(MenuSession<MonsterPlayer> player) {
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
