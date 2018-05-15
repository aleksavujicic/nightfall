package deimophobe.nightfall.common.database.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Deimophobe on 8/01/18.
 */

@SerializableAs("PlayerData")
@Entity(value = "players", noClassnameStored = true)
public class PlayerData implements Data {
	@Id
	@SuppressWarnings("unused")
	private int id;
	
	@Indexed(options = @IndexOptions(unique = true))
	public String uuid;
	
	@Embedded("cosmetics")
	public CosmeticsData cosmetics = new CosmeticsData();
	
	public PlayerData() {}
	public PlayerData(UUID uuid) {this.uuid = uuid.toString();}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	// Bukkit Configuration
	private static final String UUID_KEY = "uuid";
	private static final String COSMETICS_KEY = "cosmetics";
	
	@SuppressWarnings("unused")
	public static PlayerData deserialize(Map<String, Object> map) {
		PlayerData data = new PlayerData();
		data.uuid = (String) map.get(UUID_KEY);
		data.cosmetics = (CosmeticsData) map.get(COSMETICS_KEY);
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(UUID_KEY, uuid);
		map.put(COSMETICS_KEY, cosmetics);
		
		return map;
	}
}
