package deimophobe.nightfall.items;

import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.monster.mob.MobType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 30/04/17.
 */
public class ItemManager {
	private static ItemManager manager = new ItemManager();
	public static ItemManager getManager() {
		return manager;
	}
	
	private final static boolean ENABLED = true;
	
	private final Map<String, CustomItem> items;
	private ItemManager() {
		if (!ENABLED) return;
		
		items = new HashMap<>();
		items.putAll(addPrefix("dwarf", getDwarfItems()));
		items.putAll(addPrefix("mob", getMobItems()));
	}
	
	private final Map<String, CustomItem> getDwarfItems() {
		return DwarvenItems.getAllItems();
	}
	
	private final Map<String, CustomItem> getMobItems() {
		Map<String, CustomItem> items = new HashMap<>();
		for (MobType type : MobType.values()) {
			items.putAll(addPrefix(type.getName(), type.getItems()));
		}
		return items;
	}
	
	private static final Map<String, CustomItem> addPrefix(String prefix, Map<String, CustomItem> items) {
		Map<String, CustomItem> newItems = new HashMap<>();
		for (String key : items.keySet()) {
			newItems.put(prefix + "." + key, items.get(key));
		}
		return newItems;
	}
	
	
	public Collection<String> getNames() {
		return items.keySet();
	}
	
	public CustomItem getItem(String name) {
		return items.get(name);
	}
}
