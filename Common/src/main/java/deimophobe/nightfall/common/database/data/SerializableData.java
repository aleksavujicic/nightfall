package deimophobe.nightfall.common.database.data;

import deimophobe.nightfall.common.database.DataSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

/**
 * Created by Deimophobe on 30/01/19.
 */
abstract class SerializableData<T extends SerializableData<T>> implements Data, ConfigurationSerializable {
	
	protected SerializableData() {}
	
	@Override
	public Map<String, Object> serialize() {
		return getSerializer().serialize(self());
	}
	
	
	private T self() {
		return (T) this;
	}
	
	protected abstract DataSerializer<T> getSerializer();
}
