package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Game;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
public class Loadout {
	
	private static final int MAX_POINTS = 64;
	
	private final Set<LoadoutItem> items = new HashSet<>();
	
	void selectItem(LoadoutItem item) {
		if (items.contains(item)) {
			items.remove(item);
		} else {
			Set<LoadoutItem> categoryItems = item.getItemsInCategory();
			categoryItems.retainAll(items);
			
			if (categoryItems.size() > 1) {
				// Should only ever be one item of the same category in a loadout
				Bukkit.getLogger().severe("Loadout contains more than one category item!? " + categoryItems.toString());
				items.removeAll(categoryItems);
			} else {
				int extraPoints = 0; // The amount of extra points one would get from removing a similar item.
				if (categoryItems.size() == 1) {
					LoadoutItem categoryItem = categoryItems.iterator().next();
					extraPoints += categoryItem.getCost();
				}
				
				// If there are still points after adding this item, let it be added
				if (getRemainingPoints() + extraPoints >= item.getCost()) {
					items.removeAll(categoryItems);
					items.add(item);
				}
			}
		}
	}
	
	boolean hasItem(LoadoutItem item) {
		return items.contains(item);
	}
	
	DwarfData constructProperties() {
		DwarfData data = new DwarfData();
		for (LoadoutItem item : items) {
			item.modify(data);
		}
		return data;
	}
	
	int getRemainingPoints() {
		int usedPoints = 0;
		for (LoadoutItem item : items) {
			usedPoints += item.getCost();
		}
		return MAX_POINTS - usedPoints;
	}
	
	public void clear() {
		items.clear();
	}
	
	
	private static final Map<UUID, Loadout> loadouts = new HashMap<>();
	static Loadout getLoadout(Player player) {
		return getLoadout(player.getUniqueId());
	}
	static Loadout getLoadout(UUID uuid) {
		return loadouts.computeIfAbsent(uuid, k -> new Loadout());
	}
	
	
	// ------ SAVING AND LOADING TO FILE
	public static void setupLoadouts() {
		// Load save yaml file
		YamlConfiguration config = YamlConfiguration.loadConfiguration(getLoadoutFile());
		for (String key : config.getKeys(false)) {
			UUID uuid = UUID.fromString(key);
			Loadout loadout = fromStringList(config.getStringList(key));
			loadouts.put(uuid, loadout);
		}
		
		// TODO: Make async?
		new BukkitRunnable() {
			@Override
			public void run() {
				saveLoadouts();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 1200, 1200);
	}
	
	public static void saveLoadouts() {
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
	
	private static final String FILENAME = "loadouts.yml";
	private static File getLoadoutFile() {
		return new File(Game.getGame().getPlugin().getDataFolder(), FILENAME);
	}
	
	
	private List<String> toStringList() {
		List<String> strings = new ArrayList<>();
		for (LoadoutItem item : items) {
			strings.add(item.toString());
		}
		return strings;
	}
	
	private static Loadout fromStringList(List<String> stringList) {
		if (stringList == null) return null;
		
		Loadout loadout = new Loadout();
		for (String string : stringList) {
			LoadoutItem item = LoadoutItem.valueOf(string);
			loadout.items.add(item);
		}
		return loadout;
	}
}
