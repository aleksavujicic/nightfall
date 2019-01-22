package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.monster.mob.MobType;
import deimophobe.nightfall.monster.upgrades.UpgradeRegistry;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 16/01/19.
 */
class MonsterMenuConfig {
	private static final String CONFIG_FILENAME = "monster-menu.yml";
	
	private final NightfallPlugin plugin;
	
	private final Map<String, CustomItem> items;
	private final Map<MobType, PrimarySelectorData> primarySelectors;
	
	MonsterMenuConfig(NightfallPlugin plugin) {
		Configuration config = plugin.readInternalFileConfig(CONFIG_FILENAME);
		this.plugin = plugin;
		
		// Initialise items
		items = new HashMap<>();
		ConfigurationSection itemSection = config.getConfigurationSection("items");
		if (itemSection == null) throw new NullPointerException("Missing items section in spawn menu");
		
		for (String key : itemSection.getKeys(false)) {
			ConfigurationSection itemConfig = itemSection.getConfigurationSection(key);
			CustomItem item = CustomItem.getItem(itemConfig, "monster-menu");
			items.put(key, item);
		}
		
		
		// Initialise primarySelectors
		primarySelectors = new HashMap<>();
		ConfigurationSection primarySection = config.getConfigurationSection("primary-selectors");
		if (primarySection == null) throw new NullPointerException("Missing primary section in spawn menu");
		
		for (String key : primarySection.getKeys(false)) {
			try {
				MobType mobType = Misc.getEnumMemberFromString(key, MobType.values(), "mobType");
				if (!mobType.isPrimary()) throw new IllegalArgumentException("Mob type '" + mobType + "' for primary selector is not primary");
				
				ConfigurationSection selectorConfig = primarySection.getConfigurationSection(key);
				int cost = selectorConfig.getInt("cost");
				
				ConfigurationSection itemConfig = selectorConfig.getConfigurationSection("item");
				CustomItem item = CustomItem.getItem(itemConfig, "monster-primary-selector");
				item.applyVariable("cost", "" + cost);
				
				PrimarySelectorData selector = new PrimarySelectorData(item, cost);
				
				primarySelectors.put(mobType, selector);
			} catch (UnknownEnumElementException e) {
				NightfallPlugin.logger().warning("Unknown mob type for primary selector '" + key +"' config");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	NightfallPlugin getPlugin() {
		return plugin;
	}
	UpgradeRegistry getRegistry() {
		return plugin.getUpgradeRegistry();
	}
	
	CustomItem getItem(String name) {
		checkArgument(items.containsKey(name), "Unknown spawn item with name '%s'", name);
		return items.get(name);
	}
	
	ItemStack getItemStack(String name) {
		return getItem(name).createItemStack();
	}
	
	PrimarySelectorData getPrimarySelector(MobType mobType) {
		checkArgument(mobType.isPrimary(), "Mob type '%s' must be primary", mobType);
		checkArgument(primarySelectors.containsKey(mobType), "Mob type '%s' has no selector (even though it is primary?!)", mobType);
		
		return primarySelectors.get(mobType);
	}
}
