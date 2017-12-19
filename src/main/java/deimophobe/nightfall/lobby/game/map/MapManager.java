package deimophobe.nightfall.lobby.game.map;

import deimophobe.nightfall.lobby.NightfallLobbyPlugin;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 17/12/17.
 */
public class MapManager {
	public static MapManager getManager() { return NightfallLobbyPlugin.getPlugin().getMapManager(); }
	
	private final Map<String, GameMap> maps = new HashMap<>();
	
	public MapManager() throws IOException {
		loadMaps();
	}
	
	private void loadMaps() throws IOException {
		File mapFile = new File(GameMap.getMapFolder(), "maps.yml");
		
		if (!mapFile.exists()) throw new FileNotFoundException("maps.yml file not found in map folder.");
		
		Configuration fullMapConfig = YamlConfiguration.loadConfiguration(mapFile);;
		
		maps.clear();
		ConfigurationSection mapsConfig = fullMapConfig.getConfigurationSection("maps");
		for (String key : mapsConfig.getKeys(false)) {
			try {
				maps.put(key, new GameMap(key, mapsConfig.getConfigurationSection(key)));
			} catch (InvalidMapConfigException e) {
				NightfallLobbyPlugin.getPlugin().getLogger().severe("Malformed config for map '" + key + "'");
				e.printStackTrace();
			}
		}
	}
	
	public GameMap getMap(String id) {
		return maps.get(id);
	}
}
