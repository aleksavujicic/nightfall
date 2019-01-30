package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.*;

import java.util.*;

/**
 * Created by Deimophobe on 8/01/18.
 */

@SerializableAs("PlayerData")
@Entity(value = "players", noClassnameStored = true)
public class PlayerData extends SerializableData<PlayerData> {
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
	
	@Embedded("statistics")
	public PlayerStatsData statistics = new PlayerStatsData();
	
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
	
	
	
	private static final DataSerializer<PlayerData> SERIALIZER = new DataSerializer<>(PlayerData.class);
	@Override
	protected DataSerializer<PlayerData> getSerializer() {
		return SERIALIZER;
	}
	public static PlayerData deserialize(Map<String, Object> map) {
		return SERIALIZER.deserialize(map);
	}
}
