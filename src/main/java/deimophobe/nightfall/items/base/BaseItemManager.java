package deimophobe.nightfall.items.base;

import deimophobe.nightfall.Misc;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class BaseItemManager {
	
	public static BaseItem getItem(String name) {
		BaseItem item = baseItems.get(name.toLowerCase());
		if (item == null) throw new IllegalArgumentException("No base item named: " + name);
		
		return item;
	}
	
	public static BaseItem getErrorItem() {
		return new ErrorItem();
	}
	
	private static final Map<String, BaseItem> baseItems = new HashMap<>();
	private static void addItem(String name, BaseItem item) {
		baseItems.put(name.toLowerCase(), item);
	}
	static {
		addItem("healing_ale", new PotionItem(Color.fromRGB(93, 244, 17)));
		addItem("jimmyjuice", new PotionItem(Color.RED));
		addItem("holy_ale", new PotionItem(Color.fromRGB(17, 108, 244)));
		
		addItem("stick", new SimpleBaseItem(Material.STICK));
		
		addItem("doom_clock", new SimpleBaseItem(Material.WATCH, 0));
		
		addItem("upgrade_zombie", new SimpleBaseItem(Material.SKULL_ITEM, 2));
		
		addItem("temporary", new SimpleBaseItem(Material.RAW_FISH, 3));
		
		// Add items from base-items.yml file
		FileConfiguration config = Misc.getInternalFileConfig("base-items.yml");
		for (String key : config.getKeys(false)) {
			ConfigurationSection keyConfig = config.getConfigurationSection(key);
			Material material = Material.matchMaterial(key);
			
			for (String itemName : keyConfig.getKeys(true)) {
				int damage = keyConfig.getInt(itemName);
				
				addItem(itemName, new SimpleBaseItem(material, damage));
			}
		}
	}
	
	private static final class ErrorItem extends SimpleBaseItem {
		ErrorItem() {
			super(Material.BARRIER);
		}
	}
}
