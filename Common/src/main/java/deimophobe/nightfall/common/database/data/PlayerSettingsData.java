package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.ConfigUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 16/05/18.
 */

@SerializableAs("PlayerSettingsData")
@Embedded
public class PlayerSettingsData implements Data {
	@Property
	public boolean heroEnabled = true;
	@Property
	public boolean mobDeathMessages = false;
	
	public PlayerSettingsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	// Bukkit Configuration
	private static final String HERO_KEY = "hero-enabled";
	private static final String MOB_DEATH_KEY = "mob-death-messages";
	
	@SuppressWarnings("unused")
	public static PlayerSettingsData deserialize(Map<String, Object> map) {
		PlayerSettingsData data = new PlayerSettingsData();
		data.heroEnabled = ConfigUtil.getObjectFromMap(map, HERO_KEY, Boolean.class, true);
		data.mobDeathMessages = ConfigUtil.getObjectFromMap(map, MOB_DEATH_KEY, Boolean.class, false);
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(HERO_KEY, heroEnabled);
		map.put(MOB_DEATH_KEY, mobDeathMessages);
		
		return map;
	}
}
