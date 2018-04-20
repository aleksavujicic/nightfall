package deimophobe.nightfall.bungee.map;

import deimophobe.nightfall.bungee.Weightable;
import net.md_5.bungee.config.Configuration;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class RotationMap implements Weightable {
	
	private final Rotation rotation;
	private final GameMap map;
	private double baseWeight;
	
	public GameMap getMap() {
		return map;
	}
	
	public RotationMap(Rotation rotation, String key, Configuration config) throws InvalidRotationConfigException {
		this.rotation = rotation;
		
		this.map = GameMap.getMap(key);
		if (map == null) throw new InvalidRotationConfigException("Invalid map '" + key + "' in rotation '" + rotation.getName() + "'");
		
		checkConfigContains(config, "weight");
		baseWeight = config.getDouble("weight");
		if (baseWeight == 0) throw new InvalidRotationConfigException("Weight should not be null of map '" + key + "' in rotation '" + rotation.getName() + "'");
	}
	
	private void checkConfigContains(Configuration configuration, String path) throws InvalidRotationConfigException {
		if (!configuration.contains(path)) throw new InvalidRotationConfigException("Could not find path '" + path + "' in rotation '" + rotation.getName() + "'");
	}
	
	@Override
	public double getWeight() {
		return baseWeight;
	}
}
