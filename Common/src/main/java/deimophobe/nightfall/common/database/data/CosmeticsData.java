package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
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
public class CosmeticsData extends SerializableData<CosmeticsData> {
	@Property
	public String title = null;
	
	@Property
	public String hat = null;
	
	public CosmeticsData() {}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	
	
	
	private static final DataSerializer<CosmeticsData> SERIALIZER = new DataSerializer<>(CosmeticsData.class);
	@Override
	protected DataSerializer<CosmeticsData> getSerializer() {
		return SERIALIZER;
	}
	public static CosmeticsData deserialize(Map<String, Object> map) {
		return SERIALIZER.deserialize(map);
	}
}
