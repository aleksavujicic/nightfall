package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.Misc;
import deimophobe.dvz.menu.GameMenu;
import deimophobe.dvz.menu.MenuItem;
import deimophobe.dvz.monster.MonsterPlayer;
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
public class SpawnMenu extends GameMenu<MonsterPlayer> {
	
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
	
	@Override
	public void addItem(int i, MenuItem<MonsterPlayer> item) {
		super.addItem(i, item);
	}
	
	private void addItem(ConfigurationSection config) {
		MenuItem<MonsterPlayer> menuItem;
		switch (config.getString("type")) {
			case "mobegg":
				menuItem = SpawnEggMenuItem.getEgg(config.getString("egg"));
				break;
			
			case "doomclock":
				ItemStack item = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
				int cost = config.getInt("cost");
				int time = config.getInt("time");
				menuItem = new DoomClockItem(item, cost, time);
				break;
			
			case "upgrade":
				ConfigurationSection upgradeFile = Misc.getInternalFileConfig(config.getString("file"));
				menuItem = new SelectUpgradeMenuItem(config, upgradeFile);
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
			if (item instanceof SpawnEggMenuItem)
				((SpawnEggMenuItem)item).tryRespawn();
		}
	}
}
