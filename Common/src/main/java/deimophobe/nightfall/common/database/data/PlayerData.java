package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.ConfigUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.*;

import java.util.*;

/**
 * Created by Deimophobe on 8/01/18.
 */

@SerializableAs("PlayerData")
@Entity(value = "players", noClassnameStored = true)
public class PlayerData implements Data {
	private static final String INVALID_UUID = "INVALID";
	
	@Id
	@SuppressWarnings("unused")
	private int id;
	
	@Indexed(options = @IndexOptions(unique = true))
	public String uuid;
	
	@Property
	public int gold = 0;
	
	@Embedded("cosmetics")
	public CosmeticsData cosmetics = new CosmeticsData();
	
	@Embedded("loadout")
	public LoadoutData loadout = new LoadoutData();
	
	@Embedded("savedloaduts")
	public List<LoadoutData> savedLoadouts = new ArrayList<>();
	
	@Embedded("settings")
	public PlayerSettingsData settings = new PlayerSettingsData();
	
	public PlayerData() {}
	public PlayerData(UUID uuid) {this.uuid = uuid.toString();}
	
	// Misc helper methods
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public boolean isValid() {
		return !INVALID_UUID.equals(uuid);
	}
	
	// Bukkit Configuration
	private static final String UUID_KEY = "uuid";
	private static final String GOLD_KEY = "gold";
	private static final String COSMETICS_KEY = "cosmetics";
	private static final String LOADOUT_KEY = "loadout";
	private static final String SETTINGS_KEY = "settings";
	
	@SuppressWarnings("unused")
	public static PlayerData deserialize(Map<String, Object> map) {
		PlayerData data = new PlayerData();
		data.uuid      = ConfigUtil.getStringFromMap(map, UUID_KEY, INVALID_UUID);
		data.gold      = ConfigUtil.getIntFromMap(map, GOLD_KEY, 0);
		
		data.cosmetics     = ConfigUtil.getObjectFromMap(map, COSMETICS_KEY, CosmeticsData.class, new CosmeticsData());
		data.loadout       = ConfigUtil.getObjectFromMap(map, LOADOUT_KEY, LoadoutData.class, new LoadoutData());
		data.settings      = ConfigUtil.getObjectFromMap(map, SETTINGS_KEY, PlayerSettingsData.class, new PlayerSettingsData());
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(UUID_KEY, uuid);
		map.put(GOLD_KEY, gold);
		
		map.put(COSMETICS_KEY, cosmetics);
		map.put(LOADOUT_KEY, loadout);
		map.put(SETTINGS_KEY, settings);
		
		return map;
	}
}
