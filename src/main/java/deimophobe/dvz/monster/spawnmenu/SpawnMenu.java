package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Misc;
import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.menu.*;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class SpawnMenu extends IndexedMenu<MonsterPlayer, SpawnMenu.PageType> implements MainMenu<MonsterPlayer> {
	
	private static final int SIZE = 27;
	private static final String TITLE = "Monster Menu";
	private final FrontMenu frontMenu;
	
	private final MenuItem<MonsterPlayer> backItem;
	
	public SpawnMenu() {
		// Setup menus
		frontMenu = new FrontMenu(SIZE);
		
		// Add items to main
		Configuration spawnConfig = Misc.getInternalFileConfig("spawn-items.yml");
		for (String key : spawnConfig.getKeys(false)) {
			addItem(spawnConfig.getConfigurationSection(key));
		}
		
		backItem = getMenuItem(spawnConfig.getConfigurationSection("back"));
		
		// Setup other menus
		for (PageType pageType : PageType.values()) {
			String file = pageType.filename;
			if (file != null)
				putPage(pageType, new UpgradeMenu(Misc.getInternalFileConfig(file), this));
		}
		
		// Add them
		putPage(PageType.MAIN, frontMenu);
	}
	
	private MenuItem<MonsterPlayer> getMenuItem(ConfigurationSection config) {
		switch (config.getString("type")) {
			case "mobegg":
				return SpawnEggMenuItem.getEgg(config.getString("egg"));
			
			case "doomclock":
				ItemStack item = CustomItem.getItem(config.getConfigurationSection("item"), "monster-menu", Slot.MAIN_HAND).createItemStack();
				int cost = config.getInt("cost");
				int time = config.getInt("time");
				return new DoomClockItem(item, cost, time);
			
			case "pager":
				ItemStack item2 = CustomItem.getItem(config.getConfigurationSection("item"), "monster-menu", Slot.MAIN_HAND).createItemStack();
				PageType page = PageType.getPageType(config.getString("page"));
				return new IndexedPageChanger<>(item2, this, page);
			
			default:
				Bukkit.getLogger().warning("Could not interpret type of spawn item: " + config.getCurrentPath());
				return null;
		}
	}
	
	private void addItem(ConfigurationSection config) {
		if (!config.contains("index")) return;
		
		MenuItem<MonsterPlayer> menuItem = getMenuItem(config);
		int index = config.getInt("index", -1);
		
		if (index < -1 || index > 26) {
			Bukkit.getLogger().warning("Index must be 0-26 but got: " + index);
			return;
		}
		
		frontMenu.setItem(index, menuItem);
	}
	
	
	MenuItem<MonsterPlayer> getBackItem() {
		return backItem;
	}
	
	public void updateEggs() {
		frontMenu.updateEggs();
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}
	
	@Override
	public MonsterPlayer getDataFromPlayer(Player player) {
		return MonsterManager.getManager().getGamePlayer(player);
	}
	
	@Override
	protected PageType getDefault() {
		return PageType.MAIN;
	}
	
	enum PageType {
		MAIN, ZOMBIE_UPGRADE("zombie-upgrades.yml"), GOBO_UPGRADE
		;
		
		private final String filename;
		
		PageType() { filename = null;}
		PageType(String filename) {this.filename = filename;}
		
		public static PageType getPageType(String name) {
			return valueOf(name.toUpperCase().replace('-','_'));
		}
	}
	
	private final class FrontMenu extends SimpleMenu<MonsterPlayer> {
		
		private FrontMenu(int size) {
			super(size);
		}
		
		private void updateEggs() {
			for (MenuItem item : getMenuItems()) {
				if (item instanceof SpawnEggMenuItem)
					((SpawnEggMenuItem)item).tryRestock();
			}
		}
	}
}
