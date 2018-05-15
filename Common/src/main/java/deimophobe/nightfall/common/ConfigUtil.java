package deimophobe.nightfall.common;

import java.util.Map;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class ConfigUtil {
	
	public static int getIntFromMap(Map<String, Object> map, String key, int _default) {
		return getObjectFromMap(map, key, Integer.class, _default);
	}
	
	public static String getStringFromMap(Map<String, Object> map, String key, String _default) {
		return getObjectFromMap(map, key, String.class, _default);
	}
	
	public static <T> T getObjectFromMap(Map<String, Object> map, String key, Class<T> _class, T _default) {
		Object object = map.get(key);
		if (_class.isInstance(object)) {
			return _class.cast(object);
		} else {
			return _default;
		}
	}
}
