package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bukkit.configuration.serialization.SerializableAs;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Deimophobe on 15/05/18.
 */
@SerializableAs("LoadoutData")
@Embedded
public class LoadoutData extends SerializableData<LoadoutData> {
	@Property
	public List<String> items = new ArrayList<>();
	
	public LoadoutData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	
	
	private static final DataSerializer<LoadoutData> SERIALIZER = new DataSerializer<>(LoadoutData.class);
	@Override
	protected DataSerializer<LoadoutData> getSerializer() {
		return SERIALIZER;
	}
	public static LoadoutData deserialize(Map<String, Object> map) {
		return SERIALIZER.deserialize(map);
	}

}
