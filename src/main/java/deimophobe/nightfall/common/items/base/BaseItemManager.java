package deimophobe.nightfall.common.items.base;

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
	private static final BaseItemManager manager = new BaseItemManager();
	public static BaseItemManager getManager() { return manager; }
	
	
	
	private final Map<String, BaseItem> baseItems = new HashMap<>();
	private void addItem(String name, BaseItem item) {
		name = name.toLowerCase();
		if (baseItems.containsKey(name))
			throw new IllegalArgumentException("Trying to add base item '" + name + "' twice?!");
		baseItems.put(name, item);
	}
	
	public BaseItem getItem(String name) {
		BaseItem item = baseItems.get(name.toLowerCase());
		if (item == null) throw new IllegalArgumentException("No base item named: " + name);
		
		return item;
	}
	
	private void addPotion(String name, int red, int green, int blue) {
		addItem(name, new PotionItem(Color.fromRGB(red, green, blue)));
	}
	
	private BaseItemManager() {
		addPotion("healing_ale", 93, 244, 17);
		addPotion("jimmyjuice", 249, 204, 24);
		addPotion("hearty_ale", 115, 5, 193);
		addPotion("chug", 17, 108, 244);
		addPotion("strong", 183, 37, 18);
		addPotion("glow", 183, 37, 18);
		addPotion("potion-regeneration", 252, 47, 194);
		addPotion("potion-poison", 32, 132, 11);
		
		addItem("stick", new SimpleBaseItem(Material.STICK));
		
		addItem("doom_clock", new SimpleBaseItem(Material.WATCH, 0));
		
		addItem("upgrade_zombie", new SimpleBaseItem(Material.SKULL_ITEM, 2));
		addItem("upgrade_skeleton", new SimpleBaseItem(Material.SKULL_ITEM, 0));
		addItem("upgrade_gobo", new SimpleBaseItem(Material.SKULL_ITEM, 4));
		addItem("wither_skull", new SimpleBaseItem(Material.SKULL_ITEM, 1));
		
		BaseItem temp = new SimpleBaseItem(Material.FERMENTED_SPIDER_EYE);
		addItem("temp", temp);
		addItem("temporary", temp);
		
		// Add items from base-items.yml file
		FileConfiguration config = Misc.getInternalFileConfig("base-items.yml");
		for (String key : config.getKeys(false)) {
			ConfigurationSection keyConfig = config.getConfigurationSection(key);
			Material material = Material.matchMaterial(key);
			
			if (keyConfig == null) {
				String itemName = config.getString(key);
				addItem(itemName, new SimpleBaseItem(material));
			} else {
				for (String itemName : keyConfig.getKeys(true)) {
					int damage = keyConfig.getInt(itemName);
					
					addItem(itemName, new SimpleBaseItem(material, damage));
				}
			}
		}
	}
	
	public static BaseItem getErrorItem() {
		return new ErrorItem();
	}
	private static final class ErrorItem extends SimpleBaseItem {
		ErrorItem() {
			super(Material.BARRIER);
		}
	}
}
