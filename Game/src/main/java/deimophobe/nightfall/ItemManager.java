package deimophobe.nightfall;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.dwarf.DwarvenItems;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.configuration.ConfigurationSection;

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
		items.putAll(addPrefix("misc", getMiscItems()));
	}
	
	private Map<String, CustomItem> getDwarfItems() {
		return DwarvenItems.getAllItems();
	}
	
	private Map<String, CustomItem> getMobItems() {
		Map<String, CustomItem> items = new HashMap<>();
		for (MobType type : MobType.values()) {
			items.putAll(addPrefix(type.getName(), type.getItems()));
		}
		return items;
	}
	
	private Map<String, CustomItem> getMiscItems() {
		Map<String, CustomItem> items = new HashMap<>();
		ConfigurationSection config = NightfallPlugin.getInternalFileConfig("misc-items.yml");
		for (String key : config.getKeys(false)) {
			items.put(key, CustomItem.getItem(config.getConfigurationSection(key), LoreTemplate.DEFAULT));
		}
		return items;
	}
	
	private static Map<String, CustomItem> addPrefix(String prefix, Map<String, CustomItem> items) {
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
	
	
	// ------ ITEMS ------
	public static CustomItem getMiscItem(String name) {
		return CustomItem.getItem(NightfallPlugin.getInternalFileConfig("misc-items.yml").getConfigurationSection(name), LoreTemplate.DEFAULT);
	}
}
