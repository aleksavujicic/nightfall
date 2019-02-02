package deimophobe.nightfall.common.database;

import com.google.common.base.CaseFormat;
import deimophobe.nightfall.common.database.data.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 30/01/19.
 */
public class DataSerializer<T extends Data> {
	
	private final Set<FieldSerializer> fieldSerializers;
	private final Supplier<T> creator;
	
	public DataSerializer(Class<T> dataClass) {
		Field[] fields = dataClass.getDeclaredFields();
		
		fieldSerializers = new HashSet<>();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isStatic(mod)) continue;
			
			String key = field.getName();
			FieldSerializer fieldSerializer = new FieldSerializer(key, field);
			fieldSerializers.add(fieldSerializer);
		}
		
		try {
			Constructor<T> constructor = dataClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			this.creator = () -> {
				try {
					return constructor.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			};
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public Map<String, Object> serialize(T data) {
		Map<String, Object> map = new HashMap<>();
		for (FieldSerializer fieldSerializer : fieldSerializers) {
			fieldSerializer.addToMap(map, data);
		}
		return map;
	}
	
	public T deserialize(Map<String, Object> map) {
		T data = creator.get();
		for (FieldSerializer fieldSerializer : fieldSerializers) {
			fieldSerializer.addToData(data, map);
		}
		return data;
	}
	
	
	
	private class FieldSerializer {
		private final String key;
		private final Field field;
		
		
		private FieldSerializer(String key, Field field) {
			this.key = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, key);
			this.field = field;
			field.setAccessible(true);
		}
		
		private void addToMap(Map<String, Object> map, T data) {
			try {
				Object value = field.get(data);
				map.put(key, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		private void addToData(T data, Map<String, Object> map) {
			try {
				if (!map.containsKey(key)) return;
				
				Object value = map.get(key);
				field.set(data, value);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
