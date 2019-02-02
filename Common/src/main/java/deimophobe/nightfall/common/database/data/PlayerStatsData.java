package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.Map;

/**
 * Created by Deimophobe on 5/10/18.
 */
@SerializableAs("PlayerStatsData")
@Embedded
public class PlayerStatsData extends SerializableData<PlayerStatsData> {
	@Property
	public int gamesPlayed = 0;
	
	public PlayerStatsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	
	
	private static final DataSerializer<PlayerStatsData> SERIALIZER = new DataSerializer<>(PlayerStatsData.class);
	@Override
	protected DataSerializer<PlayerStatsData> getSerializer() {
		return SERIALIZER;
	}
	public static PlayerStatsData deserialize(Map<String, Object> map) {
		return SERIALIZER.deserialize(map);
	}
}
