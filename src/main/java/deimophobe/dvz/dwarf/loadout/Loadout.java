package deimophobe.dvz.dwarf.loadout;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.kit.elements.KitElementType;
import deimophobe.dvz.menu.SessionData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Deimophobe on 7/03/17.
 */
public class Loadout implements SessionData {
	
	private static final int MAX_POINTS = 64;
	
	private final SortedSet<LoadoutItem> items = new TreeSet<>();
	private final Map<Category, LoadoutItem> categoryItems = new HashMap<>();
	
	private final UUID playerUUID;
	
	private Loadout(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}
	
	boolean selectItem(LoadoutItem item) {
		Category cat = item.getCategory();
		
		if (items.contains(item)) {
			items.remove(item);
			if (cat != null) categoryItems.remove(cat);
			
			updateDisplay();
			return true;
		} else {
			// The amount of extra points one would get from removing a similar item.
			int extraPoints = 0;
			LoadoutItem categoryItem = null;
			if (cat != null) {
				categoryItem = categoryItems.get(cat);
				if (categoryItem != null) {
					extraPoints = categoryItem.getCost();
				}
			}
			
			// If there are still points after adding this item, let it be added
			if (getRemainingPoints() + extraPoints >= item.getCost()) {
				if (categoryItem != null) items.remove(categoryItem);
				
				items.add(item);
				if (cat != null) categoryItems.put(cat, item);
				updateDisplay();
				return true;
			}
			return false;
		}
	}
	
	boolean hasItem(LoadoutItem item) {
		return items.contains(item);
	}
	
	int getRemainingPoints() {
		int usedPoints = 0;
		for (LoadoutItem item : items) {
			usedPoints += item.getCost();
		}
		return MAX_POINTS - usedPoints;
	}
	
	void clear() {
		items.removeIf(LoadoutItem::isClearable);
		
		for (Category category : Category.values()) {
			if (category.isClearable())
				categoryItems.remove(category);
		}
		updateDisplay();
	}
	
	private void updateDisplay() { // TODO move this elsewhere maybe? Make lobbyer class maybe?
		Player player = Bukkit.getPlayer(playerUUID);
		if (player != null && Game.getGame().isLobbyPlayer(player)) {
			PlayerInventory inv = player.getInventory();
			inv.clear();
			int i = 9;
			for (LoadoutItem item : items) {
				ItemStack itemStack = item.getItemStack().clone();
				itemStack.setAmount(1);
				inv.setItem(i, itemStack);
				i++;
			}
		}
	}
	
	private static final Map<UUID, Loadout> loadouts = new HashMap<>();
	static Loadout getLoadout(Player player) {
		return getLoadout(player.getUniqueId());
	}
	static Loadout getLoadout(UUID uuid) {
		return loadouts.computeIfAbsent(uuid, k -> new Loadout(k));
	}
	
	
	DwarfData constructProperties() {
		DwarfData data = new DwarfData();
		boolean hasKit = false;
		for (LoadoutItem item : items) {
			item.modify(data);
			if (item.getCategory() == Category.KIT)
				hasKit = true;
		}
		
		// Add defaults if missing
		if (!hasKit) {
			for (Category category : Category.values()) {
				if (!categoryItems.containsKey(category)) {
					KitElementType defaultElement = category.getDefault();
					if (defaultElement != null)
						data.addElement(defaultElement);
				}
			}
		}
		return data;
	}
	
	
	// ------ SAVING AND LOADING TO FILE
	public static void setupLoadouts() {
		LoadoutMenu.loadMenu();
		
		// Load save yaml file
		YamlConfiguration config = YamlConfiguration.loadConfiguration(getLoadoutFile());
		for (String key : config.getKeys(false)) {
			UUID uuid = UUID.fromString(key);
			Loadout loadout = fromStringList(uuid, config.getStringList(key));
			loadouts.put(uuid, loadout);
		}
		
		restartAutoSaver();
	}
	
	private static BukkitRunnable autosaver = null;
	public static void restartAutoSaver() {
		if (autosaver != null)
			autosaver.cancel();
		
		autosaver = new BukkitRunnable() {
			@Override
			public void run() {
				saveLoadouts();
			}
		};
		autosaver.runTaskTimer(Game.getGame().getPlugin(), 1200, 1200);
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
	
	private static Loadout fromStringList(UUID uuid, List<String> stringList) {
		if (stringList == null) return null;
		
		Loadout loadout = new Loadout(uuid);
		for (String string : stringList) {
			LoadoutItem item = LoadoutItem.getItem(string);
			if (item != null) {
				loadout.selectItem(item);
			}
		}
		return loadout;
	}
}
