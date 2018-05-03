package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class LoadoutManager {
	private static LoadoutManager ourManager = new LoadoutManager();
	public static LoadoutManager getManager() { return ourManager; }
	
	private final Map<UUID, Loadout> loadouts = new HashMap<>();
	private final Map<String, LoadoutItem> items = new HashMap<>();
	
	
	private LoadoutManager() {
		ourManager = this;
		// Loads the menu, so that it populates the items map
		LoadoutMenu.getMenu();
		
		setupLoadouts();
	}
	
	public Loadout getLoadout(Player player) {
		return getLoadout(player.getUniqueId());
	}
	public Loadout getLoadout(UUID uuid) {
		return loadouts.computeIfAbsent(uuid, (u) -> new Loadout());
	}
	
	public int registerLoadoutItem(LoadoutItem item, String id) {
		id = id.toLowerCase().replace('_','-');
		if (items.containsKey(id))
			throw new IllegalArgumentException("Cannot register loadout item '" + id + "'. There already exists an item with same name.");
		
		items.put(id, item);
		return items.size();
	}
	
	public LoadoutItem getItem(String id) {
		LoadoutItem item = items.get(id);
		if (item == null) throw new IllegalArgumentException("Unknown loadout item: " + id);
		return item;
	}
	
	public LoadoutItem getDefaultKit() {
		return getItem("warrior-class");
	}
	
	/** Useful for checking malformed loadout items */
	public void modifyAll(LoadoutConstructable constructable) {
		for (LoadoutItem item : items.values()) {
			item.modify(new Loadout(), constructable);
		}
		for (Category category : Category.values()) {
			category.giveDefault(constructable);
		}
	}
	
	
	
	
	
	
	
	// EVERYTHING BELOW THIS IS ONLY TEMPORARY UNTIL WE GET DATABASE STUFF
	// ------ SAVING AND LOADING TO FILE ------
	
	private static File getLoadoutFile() {
		return new File(NightfallCommonPlugin.getPlugin().getDataFolder(), "loadouts.yml");
	}
	
	
	public void setupLoadouts() {
		// Load save yaml file
		YamlConfiguration config = YamlConfiguration.loadConfiguration(getLoadoutFile());
		for (String key : config.getKeys(false)) {
			UUID uuid = UUID.fromString(key);
			Loadout loadout = fromStringList(uuid, config.getStringList(key));
			loadouts.put(uuid, loadout);
		}
	}
	
	public void saveLoadouts() {
		YamlConfiguration config = new YamlConfiguration();
		for (Map.Entry<UUID, Loadout> entry : loadouts.entrySet()) {
			UUID uuid = entry.getKey();
			Loadout loadout = entry.getValue();
			config.set(uuid.toString(), loadout.toStringList());
		}
		
		try {
			config.save(getLoadoutFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Loadout fromStringList(UUID uuid, List<String> stringList) {
		if (stringList == null) return null;
		
		Loadout loadout = new Loadout();
		for (String string : stringList) {
			try {
				LoadoutItem item = getItem(string);
				loadout.selectItem(item);
			} catch (IllegalArgumentException e) {
				Bukkit.getLogger().severe("Unknown loadout item name '" + string + "' while loading " + uuid + " loadout.");
			}
		}
		return loadout;
	}
}
