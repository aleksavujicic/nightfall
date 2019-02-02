package deimophobe.nightfall.monster.spawnmenu;

import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 29/01/19.
 */
class UpgradeableMenuConfig {
	private final CustomItem spawnItem;
	private final boolean hasRebirth;
	private final int cost;
	private final CustomItem upgradeItem;
	
	static UpgradeableMenuConfig fromConfig(ConfigurationSection config) {
		boolean hasRebirth = config.getBoolean("has-rebirth", false);
		int cost = config.getInt("cost");
		
		ConfigurationSection spawnConfig = config.getConfigurationSection("spawn-item");
		CustomItem spawnItem = CustomItem.getItem(spawnConfig, "monster-egg");
		
		ConfigurationSection upgradeConfig = config.getConfigurationSection("upgrade-item");
		CustomItem upgradeItem = CustomItem.getItem(upgradeConfig, "monster-upgrade");
		upgradeItem.applyVariable("cost", "" + cost);
		
		return new UpgradeableMenuConfig(spawnItem, hasRebirth, cost, upgradeItem);
	}
	
	private UpgradeableMenuConfig(CustomItem spawnItem, boolean hasRebirth, int cost, CustomItem upgradeItem) {
		this.spawnItem = spawnItem;
		this.hasRebirth = hasRebirth;
		this.upgradeItem = upgradeItem;
		this.cost = cost;
	}
	
	CustomItem getSpawnItem() {
		return spawnItem;
	}
	
	boolean hasRebirth() {
		return hasRebirth;
	}
	
	int getCost() {
		return cost;
	}
	
	CustomItem getUpgradeItem() {
		return upgradeItem;
	}
	
}
