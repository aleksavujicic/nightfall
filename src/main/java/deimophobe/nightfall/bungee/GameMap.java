package deimophobe.nightfall.bungee;

import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class GameMap {
	private final static Map<String, GameMap> maps = new HashMap<>();
	public static GameMap getMap(String name) { return maps.get(name); }
	
	private static final File MAP_FOLDER = new File("Maps");
	public static void reloadMaps() {
		File mapFile = new File(MAP_FOLDER, "maps.yml");
		
		Configuration fullMapConfig;
		try {
			fullMapConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(mapFile);
		} catch (IOException e) {
			e.printStackTrace();
			NightfallBungeePlugin.getPlugin().getLogger().severe("Failed to load map config");
			return;
		}
		
		maps.clear();
		Configuration mapsConfig = fullMapConfig.getSection("maps");
		for (String key : mapsConfig.getKeys()) {
			maps.put(key, new GameMap(key, mapsConfig.getSection(key)));
		}
	}
	
	private final String name;
	private final File folder;
	public GameMap(String name, Configuration config) {
		this.name = name;
		this.folder = new File(MAP_FOLDER, config.getString("folder"));
	}
	
	public String getName() {
		return name;
	}
	
	public void copyToServer(SubServer server) throws IOException {
		File serverWorld = new File(server.getFullPath(), "map");
		if (serverWorld.exists()) FileUtils.forceDelete(serverWorld);
		FileUtils.copyDirectory(folder, serverWorld);
	}
}
