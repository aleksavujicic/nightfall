package deimophobe.nightfall.bungee.map;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class MapManager {
	public static MapManager getManager() { return NightfallBungeePlugin.getPlugin().getMapManager(); }
	
	private final Map<String, GameMap> maps = new HashMap<>();
	private final Map<String, Rotation> rotations = new HashMap<>();
	
	public void loadMapsAndRotations() throws IOException, InvalidRotationConfigException  {
		loadMaps();
		loadRotations();
	}
	
	private void loadMaps() throws IOException {
		File mapFile = new File(GameMap.getMapFolder(), "maps.yml");
		
		if (!mapFile.exists()) throw new FileNotFoundException("maps.yml file not found in map folder.");
		
		Configuration fullMapConfig;
		try {
			fullMapConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mapFile);
		} catch (IOException e) {
			NightfallBungeePlugin.getPlugin().getLogger().severe("Failed to load map config");
			throw e;
		}
		
		maps.clear();
		Configuration mapsConfig = fullMapConfig.getSection("maps");
		for (String key : mapsConfig.getKeys()) {
			try {
				maps.put(key, new GameMap(key, mapsConfig.getSection(key)));
			} catch (InvalidMapConfigException e) {
				NightfallBungeePlugin.getPlugin().getLogger().severe("Malformed config for map '" + key + "'");
				e.printStackTrace();
			}
		}
	}
	
	private void loadRotations() throws IOException, InvalidRotationConfigException {
		rotations.put("main", new Rotation("main"));
	}
	
	public GameMap getMap(String name) {
		return maps.get(name);
	}
	
	public GameMap getNextRotationMap() {
		return rotations.get("main").getMap();
	}
}
