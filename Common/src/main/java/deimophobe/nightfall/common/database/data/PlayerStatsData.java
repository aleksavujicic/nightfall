package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.ConfigUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 5/10/18.
 */
@SerializableAs("PlayerStatsData")
@Embedded
public class PlayerStatsData implements Data {
	@Property
	public int gamesPlayed = 0;
	
	public PlayerStatsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	// Bukkit Configuration
	private static final String GAMES_KEY = "games";
	
	@SuppressWarnings("unused")
	public static PlayerStatsData deserialize(Map<String, Object> map) {
		PlayerStatsData data = new PlayerStatsData();
		data.gamesPlayed = ConfigUtil.getObjectFromMap(map, GAMES_KEY, Integer.class, 0);
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(GAMES_KEY, gamesPlayed);
		
		return map;
	}
}
