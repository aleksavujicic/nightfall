package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.database.data.Datable;
import deimophobe.nightfall.common.database.data.PlayerSettingsData;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class PlayerSettings implements SessionData, Datable<PlayerSettingsData> {
	private final Map<Setting<?, ?>, Object> settings;
	
	public PlayerSettings(PlayerSettingsData data) {
		settings = new HashMap<>();
		
		for (Map.Entry<String, Object> entry : data.settings.entrySet()) {
			String settingName = entry.getKey();
			Setting setting = Setting.getSetting(settingName);
			Object stored = entry.getValue();
			Object value = setting.retrieveValueFromStorage(stored);
			
			settings.put(setting, value);
		}
	}
	
	@Override
	public PlayerSettingsData toData() {
		PlayerSettingsData data = new PlayerSettingsData();
		
		for (Map.Entry<Setting<?, ?>, Object> entry : settings.entrySet()) {
			Setting setting = entry.getKey();
			Object value = entry.getValue();
			Object stored = setting.formatValueForStoring(value);
			
			data.settings.put(setting.getKey(), stored);
		}
		
		return data;
	}
	
	
	// ---- General Setting Stuff ----
	
	public <V> V getValueOfSetting(Setting<V, ?> setting) {
		if (settings.containsKey(setting)) {
			return (V) settings.get(setting);
		} else {
			return setting.getDefault();
		}
	}
	
	public <V> void storeSetting(Setting<V, ?> setting, V value) {
		settings.put(setting, value);
	}
	
	public <V> void modifySetting(Setting<V, ?> setting, Function<V, V> modifier) {
		V value = getValueOfSetting(setting);
		V newValue = modifier.apply(value);
		settings.put(setting, newValue);
	}
	
	
	// --- Boolean Settings ---
	
	public boolean toggleSetting(Setting<Boolean, ?> setting) {
		modifySetting(setting, value -> !value);
		return getValueOfSetting(setting);
	}
	
	
	// --- Integer Settings ---
	
	public void incrementSetting(Setting<Integer, ?> setting) {
		incrementSetting(setting, 1);
	}
	
	public void incrementSetting(Setting<Integer, ?> setting, int increment) {
		modifySetting(setting, value -> value + increment);
	}
}
