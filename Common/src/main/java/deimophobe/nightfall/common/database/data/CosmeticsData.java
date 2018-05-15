package deimophobe.nightfall.common.database.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 15/05/18.
 */
@SerializableAs("CosmeticsData")
@Embedded
public class CosmeticsData implements Data {
	@Property
	public String title = null;
	
	@Property
	public String hat = null;
	
	public CosmeticsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	// Bukkit Configuration
	private static final String HAT_KEY = "hat";
	private static final String TITLE_KEY = "title";
	
	@SuppressWarnings("unused")
	public static CosmeticsData deserialize(Map<String, Object> map) {
		CosmeticsData data = new CosmeticsData();
		data.hat = (String) map.get(HAT_KEY);
		data.title = (String) map.get(TITLE_KEY);
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(HAT_KEY, hat);
		map.put(TITLE_KEY, title);
		
		return map;
	}
}
