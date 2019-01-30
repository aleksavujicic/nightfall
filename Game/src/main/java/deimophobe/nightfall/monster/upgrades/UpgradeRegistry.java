package deimophobe.nightfall.monster.upgrades;

import com.google.common.collect.Sets;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 17/01/19.
 */
public class UpgradeRegistry {
	private static final Set<Integer> INVALID_INDICES = Sets.newHashSet(
			0, // Spawn egg
			8, // Trash can
			9  // Rebirth
	);
	
	private final NightfallPlugin plugin;
	private final Map<String, Upgrade> upgrades;
	
	public UpgradeRegistry(NightfallPlugin plugin) {
		this.plugin = plugin;
		this.upgrades = new HashMap<>();
	}
	
	public Upgrade getUpgrade(String id) {
		Upgrade upgrade = upgrades.get(id);
		checkArgument(upgrade != null, "Unknown upgrade id '%s'", id);
		
		return upgrade;
	}
	
	public Upgrade tryGetUpgrade(String id) {
		return upgrades.get(id);
	}
	
	public Collection<Upgrade> getUpgrades(MobType mobType) {
		checkArgument(mobType.isUpgradeable(), "Mob type must be primary (got '%s')", mobType);
		String prefix = mobType.getUpgradeKey();
		
		return upgrades.entrySet()
				.stream()
				.filter(entry -> entry.getKey().startsWith(prefix))
				.map(Map.Entry::getValue)
				.collect(Collectors.toSet());
	}
	
	public Collection<String> getAllUpgradeIDs() {
		return upgrades.keySet();
	}
	
	public void importUpgradeFile(String filename) {
		Configuration config = plugin.readInternalFileConfig("upgrades/" + filename + ".yml");
		Logger logger = plugin.getLogger();
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection upgradeConfig = config.getConfigurationSection(key);
			String id = filename + "." + key;
			
			try {
				Upgrade upgrade = importUpgrade(filename, id, upgradeConfig, logger);
				upgrades.put(id, upgrade);
			} catch (BadUpgradeConfigException e) {
				logger.severe("Failed to import upgrade '" + id + "'.");
				e.printStackTrace();
			}
		}
	}
	
	private Upgrade importUpgrade(String root, String id, ConfigurationSection config, Logger logger) throws BadUpgradeConfigException {
		
		int index = config.getInt("index");
		// Check index is valid
		checkParameter(index >= 0, "Index must be positive (got '%s')", index);
		checkParameter(index <= 26, "Index must be at most 26 (got '%s')", index);
		checkParameter(!INVALID_INDICES.contains(index), "Index must be valid (got '%s')", index);
		
		boolean permanent = config.getBoolean("permanent", false);
		
		Map<Upgrade, Integer> prerequisites = new HashMap<>();
		ConfigurationSection prereqSec = config.getConfigurationSection("prereq");
		if (prereqSec != null) {
			for (String prereqKey : prereqSec.getKeys(false)) {
				String prereqId = root + "." + prereqKey;
				try {
					Upgrade prerequisite = getUpgrade(prereqId);
					int level = config.getInt("prereq." + prereqKey);
					prerequisites.put(prerequisite, level);
				} catch (IllegalArgumentException e) {
					NightfallPlugin.logger().severe("Failed to process prequisite id '" + prereqId + "' for upgrade '" + id + "'.");
					e.printStackTrace();
				}
			}
		}
		
		ConfigurationSection itemConfig = config.getConfigurationSection("item");
		checkParameter(itemConfig != null, "Item section is missing.");
		CustomItem itemTemplate = CustomItem.getItem(itemConfig, LoreTemplate.MOB_UPGRADE);
		
		List<Integer> costs = config.getIntegerList("cost");
		if (costs.size() == 0) 	costs.add(config.getInt("cost"));
		// Check costs are positive
		for (int cost : costs) {
			checkParameter(cost > 0, "All costs must be strictly positive (got '%s')", cost);
		}
		
		
		ConfigurationSection valueSection = config.getConfigurationSection("values");
		ConfigurationSection defaultSection = config.getConfigurationSection("defaults");
		
		if (permanent) {
			int cost = costs.get(0);
			Map<String, Object> values = new HashMap<>();
			if (valueSection != null) {
				for (String valueKey : valueSection.getKeys(false)) {
					values.put(valueKey, valueSection.get(valueKey));
				}
			}
			
			return new InfiniteUpgrade(id, cost, values, prerequisites, index, itemTemplate);
		} else {
			Map<String, List<?>> values = new HashMap<>();
			if (valueSection != null) {
				for (String valueKey : valueSection.getKeys(false)) {
					values.put(valueKey, valueSection.getList(valueKey));
				}
			}
			Map<String, Object> defaultValues = new HashMap<>();
			if (defaultSection != null) {
				for (String defaultKey : defaultSection.getKeys(false)) {
					if (!values.containsKey(defaultKey)) {
						logger.warning("Defaults key '" + defaultKey + "' has no corresponding value.");
						continue;
					}
					defaultValues.put(defaultKey, defaultSection.get(defaultKey));
				}
			}
			
			return new FiniteUpgrade(id, costs, values, defaultValues, prerequisites, index, itemTemplate);
		}
	}
	
	private static void checkParameter(boolean expression, String message, Object... objects) throws BadUpgradeConfigException {
		if (!expression) {
			throw new BadUpgradeConfigException(
					String.format(message, objects)
			);
		}
	}
	
	private static final class BadUpgradeConfigException extends Exception {
		private BadUpgradeConfigException(String message) {
			super(message);
		}
		private BadUpgradeConfigException(String message, Exception cause) {
			super(message, cause);
		}
	}
}
