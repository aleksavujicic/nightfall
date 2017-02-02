package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.monster.MonsterPlayer;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class SpawnManager {
	private static SpawnManager manager = new SpawnManager();
	public static SpawnManager getManager() {
		return manager;
	}
	private SpawnManager() {}
	
	
	private Map<Integer, MenuItem> menuItems;
	
	
	private final InventoryHolder MOB_MENU_HOLDER = new InventoryHolder() {
		@Override
		public Inventory getInventory() {
			Inventory guiInventory = Bukkit.createInventory(MOB_MENU_HOLDER, 27, "Pick a Monster");
			
			for (Map.Entry<Integer, MenuItem> entry : menuItems.entrySet()) {
				int index = entry.getKey();
				MenuItem item = entry.getValue();
				if (!item.isAvailable()) continue;
				
				guiInventory.setItem(index, item.getDisplayItem());
			}
			
			return guiInventory;
		}
	};
	
	// TODO MAKE CHANGES OVER TIME RATHER THAN BUILD EACH TIME?
	public Inventory getMobMenu() {
		return MOB_MENU_HOLDER.getInventory();
	}
	
	public boolean isMobSpawnMenu(Inventory inv) {
		return (inv != null && inv.getHolder() == MOB_MENU_HOLDER);
	}
	
	public void setup() {
		Plugin plugin = Game.getGame().getPlugin();
		Configuration spawnConfig = YamlConfiguration.loadConfiguration(plugin.getResource("spawn-items.yml"));
		menuItems = new HashMap<>();
		for (String key : spawnConfig.getKeys(false)) {
			addItem(spawnConfig.getConfigurationSection(key));
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateEggs();
			}
		}.runTaskTimer(plugin, 1, 300);
	}
	
	private void addItem(ConfigurationSection config) {
		MenuItem menuItem;
		switch (config.getString("type")) {
			case "mobegg":
				menuItem = SpawnEgg.getEgg(config.getString("egg"));
				break;
				
			case "doomclock":
				ItemStack item = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
				int cost = config.getInt("cost");
				int time = config.getInt("time");
				menuItem = new DoomClockItem(item, cost, time);
				break;
				
			default:
				Bukkit.getLogger().warning("Could not interpret type of spawn item: " + config.getCurrentPath());
				return;
		}
		
		int index = config.getInt("index", -1);
		if (index < 0 || index > 26) {
			Bukkit.getLogger().warning("Index must be 0-26 but got: " + index);
			return;
		}
		
		menuItems.put(index, menuItem);
	}
	
	public boolean spawnMob(int i, MonsterPlayer monster) {
		MenuItem item = menuItems.get(i);
		if (item != null && item.isAvailable())
			return item.select(monster);
		return false;
	}
	
	
	private void updateEggs() {
		for (MenuItem item : menuItems.values()) {
			if (item instanceof SpawnEgg)
				((SpawnEgg)item).tryRespawn();
		}
	}
}
