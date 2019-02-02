package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.Data;
import deimophobe.nightfall.common.items.CustomItem;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deimophobe on 2/02/19.
 */
class SettingsMenuConfig {
	private static final String SETTINGS = "settings";
	private static final String SIZE = "size";
	
	private final Set<ItemConfig> itemConfigs = new HashSet<>();
	private final int size;
	
	SettingsMenuConfig() {
		this.size = 9;
	}
	
	SettingsMenuConfig(ConfigurationSection config) throws MalformedConfigurationException {
		checkConfigContains(config, SETTINGS);
		ConfigurationSection allSettingsConfig = config.getConfigurationSection(SETTINGS);
		for (String settingKey : allSettingsConfig.getKeys(false)) {
			ConfigurationSection settingConfig = allSettingsConfig.getConfigurationSection(settingKey);
			try {
				ItemConfig itemConfig = new ItemConfig(settingConfig);
				itemConfigs.add(itemConfig);
			} catch (MalformedConfigurationException e) {
				NightfallCommonPlugin.logger().severe("Failed to process setting menu item: " + settingKey);
				e.printStackTrace();
			}
		}
		
		checkConfigContains(config, SIZE);
		size = config.getInt(SIZE);
	}
	
	Set<ItemConfig> getItemConfigs() {
		return itemConfigs;
	}
	
	int getSize() {
		return size;
	}
	
	class ItemConfig {
		private static final String INDEX = "index";
		private static final String TEMPLATE = "item";
		private static final String ENABLED = "enabled-text";
		private static final String DISABLED = "disabled-text";
		
		private final Setting<Boolean, ?> setting;
		private final int index;
		private final CustomItem template;
		private final String enabledText;
		private final String disabledText;
		
		ItemConfig(ConfigurationSection config) throws MalformedConfigurationException {
			String settingName = config.getName();
			Setting<?, ?> settingCandidate = Setting.getSetting(settingName);
			check(settingCandidate != null, "No setting named '%s'", settingName);
			Class<?> valueType = settingCandidate.getValueType();
			check(Boolean.class.isAssignableFrom(valueType), "Setting value type must be boolean (got %s)", valueType);
			this.setting = (Setting<Boolean, ?>) settingCandidate;
			
			checkConfigContains(config, INDEX);
			this.index = config.getInt(INDEX);
			
			checkConfigContains(config, TEMPLATE);
			this.template = CustomItem.getItem(config.getConfigurationSection(TEMPLATE), "settings");
			
			this.enabledText = config.getString(ENABLED, "").trim();
			this.disabledText = config.getString(DISABLED, "").trim();
		}
		
		Setting<Boolean, ?> getSetting() {
			return setting;
		}
		
		int getIndex() {
			return index;
		}
		
		CustomItem getTemplate() {
			return template;
		}
		
		String getEnabledText() {
			return enabledText;
		}
		
		String getDisabledText() {
			return disabledText;
		}
	}
	
	
	private static void checkConfigContains(ConfigurationSection config, String section) throws MalformedConfigurationException {
		check(config.contains(section), "Config must have a '%s' section", section);
	}
	
	private static void check(boolean expression, String errorMessage, Object... objects) throws MalformedConfigurationException {
		if (expression) return;
		
		throw new MalformedConfigurationException(
				String.format(errorMessage, objects)
		);
	}
}
