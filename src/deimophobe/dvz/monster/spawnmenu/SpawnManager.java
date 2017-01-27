package deimophobe.dvz.monster.spawnmenu;

import deimophobe.dvz.Game;
import deimophobe.dvz.monster.PlayerMonster;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
	
	
	private final InventoryHolder MOB_MENU_HOLDER = new InventoryHolder() {
		@Override
		public Inventory getInventory() {
			Inventory guiInventory = Bukkit.createInventory(MOB_MENU_HOLDER, 27, "Pick a Monster");
			
			for (SpawnEgg egg : activeEggs.values()) {
				if (egg == null) continue;
				if (!egg.canSpawn()) continue;
				
				guiInventory.setItem(egg.getIndex(), egg.getEgg());
			}
			
			return guiInventory;
		}
	};
	private Map<Integer, SpawnEgg> activeEggs;
	
	public Inventory getMobMenu() {
		return MOB_MENU_HOLDER.getInventory();
		
		// TODO MAKE CHANGES OVER TIME RATHER THAN BUILD EACH TIME?
	}
	
	public boolean isMobSpawnMenu(Inventory inv) {
		return (inv.getHolder() == MOB_MENU_HOLDER);
	}
	
	public void spawnMob(int i, PlayerMonster monster) {
		SpawnEgg egg = activeEggs.get(i);
		if (egg != null && egg.canSpawn())
			egg.spawn(monster);
	}
	
	public void setup() {
		Plugin plugin = Game.getGame().getPlugin();
		Configuration spawnConfig = YamlConfiguration.loadConfiguration(plugin.getResource("spawn-eggs.yml"));
		activeEggs = new HashMap<Integer, SpawnEgg>();
		for (String key : spawnConfig.getKeys(false)) {
			SpawnEgg egg = SpawnEgg.createEgg(spawnConfig.getConfigurationSection(key));
			activeEggs.put(egg.getIndex(), egg);
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				updateEggs();
			}
		}.runTaskTimer(plugin, 1, 300);
	}
	
	private void updateEggs() {
		for (SpawnEgg egg : activeEggs.values()) {
			egg.tryRespawn();
		}
	}
}
