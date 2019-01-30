package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.Map;

/**
 * Created by Deimophobe on 16/05/18.
 */

@SerializableAs("PlayerSettingsData")
@Embedded
public class PlayerSettingsData extends SerializableData<PlayerSettingsData> {
	@Property
	public boolean heroEnabled = true;
	@Property
	public boolean mobDeathMessages = false;
	
	public PlayerSettingsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final DataSerializer<PlayerSettingsData> SERIALIZER = new DataSerializer<>(PlayerSettingsData.class);
	@Override
	protected DataSerializer<PlayerSettingsData> getSerializer() {
		return SERIALIZER;
	}
	public static PlayerSettingsData deserialize(Map<String, Object> map) {
		return SERIALIZER.deserialize(map);
	}
}
