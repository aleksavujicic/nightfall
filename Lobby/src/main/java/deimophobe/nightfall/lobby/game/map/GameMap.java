package deimophobe.nightfall.lobby.game.map;

import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

/**
 * Created by Deimophobe on 8/12/17.
 */
public class GameMap {
	private static final File MAP_FOLDER = new File("maps");
	public static File getMapFolder() { return MAP_FOLDER; }
	public static GameMap getMap(String name) { return MapManager.getManager().getMap(name); }
	
	private final String id;
	private final String displayName;
	
	public String getId() {
		return id;
	}
	public String getDisplayName() {
		return displayName;
	}
	
	public GameMap(String id, ConfigurationSection config) throws InvalidMapConfigException {
		this.id = id;
		
		checkConfigContains(config, "name");
		this.displayName = config.getString("name");
	}
	
	private void checkConfigContains(ConfigurationSection configuration, String path) throws InvalidMapConfigException {
		if (!configuration.contains(path)) throw new InvalidMapConfigException("Could not find path '" + path + "' in map '" + id +"'");
	}
}
