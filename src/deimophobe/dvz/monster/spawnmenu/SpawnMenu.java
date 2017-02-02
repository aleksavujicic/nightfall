package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.menu.Menu;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.mob.MobType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Deimophobe on 2/02/17.
 */
public class SpawnMenu extends Menu {
	
	public SpawnMenu() {
		super("Pick a monster", 3);
	}
	
	public void setup() {
		Plugin plugin = Game.getGame().getPlugin();
		Configuration spawnConfig = YamlConfiguration.loadConfiguration(plugin.getResource("spawn-items.yml"));
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
			
			case "upgrade":
				ItemStack item2 = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
				MobType type = MobType.getMobType(config.getString("upgrade"));
				menuItem = new SelectUpgradeMenuItem(item2, type);
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
		
		addItem(index, menuItem);
	}
	
	
	private void updateEggs() {
		for (MenuItem item : getItems()) {
			if (item instanceof SpawnEgg)
				((SpawnEgg)item).tryRespawn();
		}
	}
}
