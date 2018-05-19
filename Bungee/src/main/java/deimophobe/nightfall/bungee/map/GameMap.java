package deimophobe.nightfall.bungee.map;

import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.md_5.bungee.config.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class GameMap {
	private static final File MAP_FOLDER = new File("maps");
	public static File getMapFolder() { return MAP_FOLDER; }
	public static GameMap getMap(String name) { return MapManager.getManager().getMap(name); }
	
	private final String id;
	private final String displayName;
	private final File folder;
	
	public String getId() {
		return id;
	}
	public String getDisplayName() {
		return displayName;
	}
	
	public GameMap(String id, Configuration config) throws InvalidMapConfigException {
		this.id = id;
		
		checkConfigContains(config, "name");
		checkConfigContains(config, "folder");
		
		this.displayName = config.getString("name");
		this.folder = new File(MAP_FOLDER, config.getString("folder"));
	}
	
	private void checkConfigContains(Configuration configuration, String path) throws InvalidMapConfigException {
		if (!configuration.contains(path)) throw new InvalidMapConfigException("Could not find path '" + path + "' in map '" + id +"'");
	}
	
	public void copyToServer(SubServer server) throws IOException {
		File serverWorld = new File(server.getFullPath(), "map");
		if (serverWorld.exists()) FileUtils.forceDelete(serverWorld);
		FileUtils.copyDirectory(folder, serverWorld);
	}
}
