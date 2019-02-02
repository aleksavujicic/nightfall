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
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 16/01/19.
 */
class MonsterMenuConfig {
	private static final String CONFIG_FILENAME = "monster-menu.yml";
	
	private final NightfallPlugin plugin;
	
	private final Map<String, CustomItem> items;
	private final Map<MobType, UpgradeableMenuConfig> upgradeableMobConfigs;
	private final RebirthItem rebirthItem;
	
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
		upgradeableMobConfigs = new HashMap<>();
		ConfigurationSection upgradeSection = config.getConfigurationSection("upgradeable-mobs");
		if (upgradeSection == null) throw new NullPointerException("Missing upgrade section in spawn menu");
		
		for (String key : upgradeSection.getKeys(false)) {
			try {
				MobType mobType = Misc.getEnumMemberFromString(key, MobType.values(), "mobType");
				if (!mobType.isUpgradeable()) throw new IllegalArgumentException("Mob type '" + mobType + "' for primary selector is not primary");
				
				ConfigurationSection mobConfig = upgradeSection.getConfigurationSection(key);
				
				UpgradeableMenuConfig upgradeableMenuConfig = UpgradeableMenuConfig.fromConfig(mobConfig);
				upgradeableMobConfigs.put(mobType, upgradeableMenuConfig);
			} catch (UnknownEnumElementException e) {
				NightfallPlugin.logger().warning("Unknown mob type for primary selector '" + key +"' config");
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		ItemStack rebirthStack = getItemStack("rebirth");
		rebirthItem = new RebirthItem(rebirthStack);
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
	
	UpgradeableMenuConfig getMenuConfig(MobType mobType) {
		checkArgument(mobType.isUpgradeable(), "Mob type '%s' must be upgradeable", mobType);
		checkArgument(upgradeableMobConfigs.containsKey(mobType), "Mob type '%s' has no menu config (even though it is upgradeable?!)", mobType);
		
		return upgradeableMobConfigs.get(mobType);
	}
	
	Set<Map.Entry<MobType, UpgradeableMenuConfig>> getMenuConfigs() {
		return upgradeableMobConfigs.entrySet();
	}
	
	RebirthItem getRebirthItem() {
		return rebirthItem;
	}
}
