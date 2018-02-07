package deimophobe.nightfall.common.items.base;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class BaseItemManager {
	private static final Pattern POTION_PATTERN = Pattern.compile("!potion\\{\\s*(\\d+)\\s*,\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\}");
	private static final Pattern DEFAULT_PATTERN = Pattern.compile("(?<material>\\w+)(?::(?<damage>\\d+))?");
	
	private final BaseItem TEMPORARY_ITEM = new SimpleBaseItem(Material.FERMENTED_SPIDER_EYE);
	private final BaseItem ERROR_ITEM = new SimpleBaseItem(Material.BARRIER);
	
	private static final BaseItemManager manager = new BaseItemManager();
	public static BaseItemManager getManager() { return manager; }
	
	
	
	private final Map<String, BaseItem> baseItems = new HashMap<>();
	public BaseItem getItem(String name) {
		name = name.toLowerCase();
		
		BaseItem item = baseItems.get(name);
		if (item == null) throw new IllegalArgumentException("No base item named: " + name);
		
		return item;
	}
	
	public BaseItem getErrorItem() {
		return ERROR_ITEM;
	}
	
	/**
	 * @deprecated The temporary item should never be accessed directly.
	 * @return The temporary BaseItem
	 */
	@Deprecated
	public BaseItem getTempItem() { return TEMPORARY_ITEM; }
	
	private BaseItemManager() {
		// Add items from base-items.yml file
		FileConfiguration config = NightfallCommonPlugin.getInternalFileConfig("base-items.yml");
		for (String key : config.getKeys(true)) {
			if (config.getConfigurationSection(key) != null) continue;
			
			String item = config.getString(key);
			try {
				baseItems.put(key.toLowerCase(), createBaseFromConfig(item));
			} catch (InvalidBaseItemConfigException e) {
				NightfallCommonPlugin.getPlugin().getLogger().severe("Could not process base item: " + key);
				e.printStackTrace();
			}
		}
	}
	
	private BaseItem createBaseFromConfig(String item) throws InvalidBaseItemConfigException {
		if (item.equals("#temp") || item.equals("#temporary")) {
			return TEMPORARY_ITEM;
		} else if (item.startsWith("#")) {
			String referenceName = item.substring(1);
			BaseItem referenceitem = getItem(referenceName);
			if (referenceitem == null) {
				throw new InvalidBaseItemConfigException("Reference item '" + referenceName + "' does not exist.");
			}
			
			return referenceitem;
		} else if (item.startsWith("!potion")) {
			Matcher matcher = POTION_PATTERN.matcher(item);
			
			if (!matcher.matches()) {
				throw new InvalidBaseItemConfigException("Potion format is invalid.");
			}
			
			int r = Integer.parseInt(matcher.group(1));
			int g = Integer.parseInt(matcher.group(2));
			int b = Integer.parseInt(matcher.group(3));
			
			return new PotionItem(Color.fromRGB(r,g,b));
		} else {
			Matcher matcher = DEFAULT_PATTERN.matcher(item);
			
			if (!matcher.matches()) {
				throw new InvalidBaseItemConfigException("Format is invalid.");
			}
			
			String materialName = matcher.group("material");
			Material material = Material.matchMaterial(materialName);
			if (material == null) {
				throw new InvalidBaseItemConfigException("Unknown material: " + materialName);
			}
			
			String damageString = matcher.group("damage");
			if (damageString != null) {
				int damage = Integer.parseInt(damageString);
				return new SimpleBaseItem(material, damage);
			} else {
				return new SimpleBaseItem(material);
			}
		}
	}
	
	private static class InvalidBaseItemConfigException extends Exception {
		private InvalidBaseItemConfigException(String s) { super(s); }
	}
}
