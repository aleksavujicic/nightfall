package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.loadout.item.LoadoutItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
		return items.get(id);
	}
	
	public LoadoutItem getDefaultKit() {
		return getItem("warrior-class");
	}
	
	
	
}
