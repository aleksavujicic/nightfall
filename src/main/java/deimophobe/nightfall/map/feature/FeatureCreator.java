package deimophobe.nightfall.map.feature;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 1/07/17.
 */
public class FeatureCreator {
	private static final Map<String, Class<? extends  MapFeature>> FEATURES;
	static {
		FEATURES = new HashMap<>();
		FEATURES.put("tp-pads", TeleportPad.class);
		FEATURES.put("nro-map", NroMap.class);
	}
	
	// Uses seperate name parameter instead of config.getName() as config might be null.
	public static MapFeature createFeature(GameMap map, String name, ConfigurationSection config) throws InvalidMapConfigException {
		Class<? extends MapFeature> featureClass = FEATURES.get(name);
		
		if (featureClass == null)
			throw new InvalidMapConfigException("No map feature called " + name);
		
		MapFeature feature;
		try {
			feature = featureClass.getDeclaredConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Unable to find constructor for map feature '" + name + "'", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to access constructor of map feature '" + name+ "'", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("Cannot create abstract map feature '" + name + "'", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception thrown in constructor of map feature '" + name + "'", e);
		}
		
		feature.activate(map, config);
		return feature;
	}
}
