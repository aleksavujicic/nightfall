package deimophobe.dvz.map;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.plague.Plague;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Deimophobe on 17/03/17.
 */
public class MapManager {
	private static MapManager manager = new MapManager();
	public static MapManager getManager() {
		return manager;
	}
	
	private final List<String> worlds = new ArrayList<>(Arrays.asList("Nightfall1","Nightfall2","Nightfall3"));
	private int worldIndex = 0;
	
	private final Set<String> maps = new HashSet<>();
	
	private final File mapConfigFolder;
	private final File mapWorldFolder;
	
	private File mapsConfigFile;
	
	private boolean loading = false;
	
	private MapManager() {
		// Map config files and folders
		mapsConfigFile = new File(DvZPlugin.getPlugin().getDataFolder(), "maps.yml");
		mapConfigFolder = new File(DvZPlugin.getPlugin().getDataFolder(), "maps");
		mapWorldFolder = new File(Bukkit.getWorldContainer(), "maps");
		
		if (!mapsConfigFile.exists())
			DvZPlugin.getPlugin().saveResource("maps.yml", false);
		
		Configuration mapConfig = YamlConfiguration.loadConfiguration(mapsConfigFile);
		enabled = mapConfig.getBoolean("enabled", true);
		
		loadConfig();
	}
	
	// ~~~~~ MAP CONFIG ~~~~~
	
	private final boolean enabled;
	public boolean isEnabled() {
		return enabled;
	}
	public void setMapsEnabled(boolean enabled) throws IOException {
		YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapsConfigFile);
		mapConfig.set("enabled", enabled);
		mapConfig.save(mapsConfigFile);
	}
	
	public void loadConfig() {
		// Find all maps in config folder and add them to list of maps
		maps.clear();
		File[] files = mapConfigFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				String name = file.getName();
				if (FilenameUtils.isExtension(name, "yml")) {
					maps.add(FilenameUtils.getBaseName(name));
				}
			}
		} else {
			
		}
		
	}
	
	public boolean isMap(String map) {
		return maps.contains(map);
	}
	
	public Set<String> getMaps() {
		return maps;
	}
	
	// ~~~~~ MAP LOADING ~~~~~
	
	public GameMap loadNextMap() {
		// Load normal world if disabled
		if (!enabled) {
			Bukkit.getLogger().warning("Map loading disabled, loading default map.");
			return loadDefaultMap();
		} else {
			return loadRandomMap();
		}
	}
	
	private GameMap loadDefaultMap() {
		World world = Bukkit.getWorlds().get(0);
		FileConfiguration defaultMapConfig = Misc.getInternalFileConfig("default-map.yml");
		try {
			return new GameMap(defaultMapConfig, world);
		} catch (InvalidMapConfigException e) {
			throw new RuntimeException("Default map config is invalid, can't start game",e);
		}
	}
	
	private GameMap loadRandomMap() {
		String map = Misc.getRandom(maps);
		return loadMap(YamlConfiguration.loadConfiguration(new File(mapConfigFolder, map + ".yml")));
	}
	
	private GameMap loadMap(FileConfiguration config) {
		// Don't do anything if disabled
		if (!enabled)
			throw new IllegalStateException("Attempted to load map while map loading is disabled");
		
		String mapName = config.getString("world");
		
		if (loading)
			throw new IllegalStateException("Attempted to load another map while loading");
		loading = true;
		
		World world = null;
		try {
			world = createMapWorld(mapName);
			return new GameMap(config, world);
		} catch (MapLoadingException | InvalidMapConfigException e) {
			e.printStackTrace();
			Bukkit.unloadWorld(world, false);
			return loadDefaultMap();
		} finally {
			loading = false;
		}
	}
	
	public void unloadMap(GameMap map) {
		World mapWorld = map.getWorld();
		World defaultWorld = Bukkit.getWorlds().get(0);
		
		map.unload();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.teleport(defaultWorld.getSpawnLocation());
		}
		
		boolean success = Bukkit.unloadWorld(mapWorld, false);
		if (!success)
			Bukkit.getLogger().severe("Failed to unload world + " );
		
	}
	
	private World createMapWorld(String mapFilename) throws MapLoadingException {
		Bukkit.getLogger().info("Begin creation of map world: " + mapFilename);
		
		String worldFilename = getNextWorldName();
		
		// Figure out stored map folder and folder of world to play on.
		File mapFolder = new File(mapWorldFolder, mapFilename);
		File worldFolder = new File(Bukkit.getWorldContainer(), worldFilename);
		
		// If map folder is empty
		if (!mapFolder.exists())
			throw new MapLoadingException("GameMap world " + mapFilename + " does not exist");
		
		// Delete world folder if it exists
		try {
			FileUtils.deleteDirectory(worldFolder);
		} catch (IOException e) {
			throw new MapLoadingException("Failed to delete existing world folder: " + worldFilename);
		}
		
		// Copy map over
		try {
			FileUtils.copyDirectory(mapFolder, worldFolder);
		} catch (IOException e) {
			throw new MapLoadingException("Failed to copy map folder to world folder.", e);
		}
		
		File uidFile = new File(worldFolder, "uid.dat");
		uidFile.delete();
		File lockFile = new File(worldFolder, "session.lock");
		lockFile.delete();
		
		if (uidFile.exists())
			throw new MapLoadingException("Failed to delete uid file.");
		if (lockFile.exists())
			throw new MapLoadingException("Failed to delete lock file.");
		
		return Bukkit.createWorld(new WorldCreator(worldFilename));
	}
	
	private String getNextWorldName() {
		worldIndex++;
		worldIndex = worldIndex % worlds.size();
		return worlds.get(worldIndex);
	}
}
