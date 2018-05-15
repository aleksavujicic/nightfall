package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.ConfigUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 15/05/18.
 */
@SerializableAs("LoadoutData")
@Embedded
public class LoadoutData implements Data {
	@Property
	public List<String> items = new ArrayList<>();
	
	public LoadoutData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	// Bukkit Configuration
	private static final String ITEMS_KEY = "items";
	
	@SuppressWarnings("unused")
	public static LoadoutData deserialize(Map<String, Object> map) {
		LoadoutData data = new LoadoutData();
		data.items = ConfigUtil.getObjectFromMap(map, ITEMS_KEY, List.class, new ArrayList<String>());
		
		return data;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put(ITEMS_KEY, items);
		
		return map;
	}

}
