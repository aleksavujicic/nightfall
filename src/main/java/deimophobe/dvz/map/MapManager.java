package deimophobe.dvz.map;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
	
	private final Map<String, File> maps = new HashMap<>();
	private final File mapConfigFile;
	private final File mapWorldFolder;
	
	private boolean loading = false;
	
	private MapManager() {
		// Save default config
		World defaultWorld = Bukkit.getWorlds().get(0);
		File configFile = getNightfallConfig(defaultWorld);
		InputStream is = DvZPlugin.getPlugin().getResource("default-map.yml");
		try {
			Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Bukkit.getLogger().severe("Failed to save default config!");
			e.printStackTrace();
		}
		
		
		// Map config files and folders
		mapConfigFile = new File(DvZPlugin.getPlugin().getDataFolder(), "maps.yml");
		mapWorldFolder = new File(Bukkit.getWorldContainer(), "maps");
		
		if (!mapConfigFile.exists()) {
			DvZPlugin.getPlugin().saveResource("maps.yml", false);
			Bukkit.getLogger().warning("No maps.yml file found - creating default. This may not have the neccesary maps.");
		}
		
		if (!mapWorldFolder.exists()) {
			Bukkit.getLogger().warning("No maps folder found - creating empty folder. This has no maps and will disable map loading.");
			boolean success = mapWorldFolder.mkdir();
			if (!success) {
				Bukkit.getLogger().severe("Failed to create map folder!?");
			}
		}
		
		
		// Load config
		reloadConfig();
		
		// Check maps exist
		if (maps.isEmpty()) {
			Bukkit.getLogger().severe("No maps were found. Disabling map loading.");
			enabled = false;
			return;
		}
		
		Configuration mapConfig = YamlConfiguration.loadConfiguration(mapConfigFile);
		enabled = mapConfig.getBoolean("enabled", true);
	}
	
	// ~~~~~ MAP CONFIG ~~~~~
	
	private final boolean enabled;
	public boolean isEnabled() {
		return enabled;
	}
	public void setMapsEnabled(boolean enabled) throws IOException {
		YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapConfigFile);
		mapConfig.set("enabled", enabled);
		mapConfig.save(mapConfigFile);
	}
	
	public Set<String> getMaps() {
		return maps.keySet();
	}
	
	public void reloadConfig() {
		if (!mapWorldFolder.exists()) {
			Bukkit.getLogger().severe("No map folder found - no maps will be created.");
			return;
		}
		Configuration mapConfig = YamlConfiguration.loadConfiguration(mapConfigFile);
		ConfigurationSection mapSection = mapConfig.getConfigurationSection("maps");

		if (mapSection == null) {
			Bukkit.getLogger().severe("No section found for maps in maps.yml - no maps will be created.");
			return;
		}

		for (String mapName : mapSection.getKeys(false)) {
			String mapFilename = mapSection.getString(mapName);
			if (mapFilename == null) {
				Bukkit.getLogger().severe("No map folder given for key '" + mapName +"' in maps.yml.");
				continue;
			}
			
			File mapFile = new File(mapWorldFolder, mapFilename);
			if (!mapFile.exists()) {
				Bukkit.getLogger().severe("No map found in map folder with name '" + mapFilename +"' in maps.yml.");
				continue;
			}
			
			maps.put(mapName, mapFile);
		}
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
		try {
			return new GameMap(world);
		} catch (InvalidMapConfigException e) {
			throw new RuntimeException("Default map config is invalid, can't start game",e);
		}
	}
	
	private GameMap loadRandomMap() {
		String mapName = Misc.getRandom(maps.keySet());
		return loadMap(maps.get(mapName));
	}
	
	private GameMap loadMap(File mapFolder) {
		Bukkit.getLogger().info("Begin loading map");
		// Don't do anything if disabled
		if (!enabled)
			throw new IllegalStateException("Attempted to load map while map loading is disabled");
				
		if (loading)
			throw new IllegalStateException("Attempted to load another map while loading");
		loading = true;
		
		World world = null;
		try {
			world = createMapWorld(mapFolder);
			return new GameMap(world);
		} catch (MapLoadingException | InvalidMapConfigException e) {
			e.printStackTrace();
			Bukkit.unloadWorld(world, false);
			return loadDefaultMap();
		} finally {
			loading = false;
			Bukkit.getLogger().info("Finished loading map");
		}
	}
	
	private World createMapWorld(File mapFolder) throws MapLoadingException {
		Bukkit.getLogger().info("Begin creation of map world: " + mapFolder.toString());
		
		String worldFilename = getNextWorldName();
		
		// Figure out stored map folder and folder of world to play on.
		File worldFolder = new File(Bukkit.getWorldContainer(), worldFilename);
		
		// If map folder is empty
		if (!mapFolder.exists())
			throw new MapLoadingException("Game map folder " + mapFolder.toString() + " does not exist");
		
		// Delete world folder if it exists
		try {
			if (worldFolder.exists()) {
				Bukkit.getLogger().warning("World folder '"+worldFilename+"' exists!");
				FileUtils.deleteDirectory(worldFolder);
			}
		} catch (IOException e) {
			throw new MapLoadingException("Failed to delete existing world folder: " + worldFilename, e);
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
		
		World world = Bukkit.createWorld(new WorldCreator(worldFilename));
		Bukkit.getLogger().info("Finished creating world.");
		return world;
	}
	
	public void unloadAndDeleteWorld(World world) {
		Bukkit.getLogger().info("Begin unloading map.");
		World defaultWorld = Bukkit.getWorlds().get(0);
		World gameWorld = GameMap.getCurrentMap().getWorld();
		
		World safeWorld;
		if (world == defaultWorld) {
			Bukkit.getLogger().warning("Cannot unload default world (this is normal if map loading is disabled).");
			return;
		} else if (world == gameWorld) {
			Bukkit.getLogger().warning("Attempting to unload game world (this is normal if server is stopping/reloading).");
			safeWorld = defaultWorld;
		} else {
			safeWorld = gameWorld;
		}
		
		for (Player player : world.getPlayers()) {
			if (player.isDead())
				player.spigot().respawn();
			player.teleport(safeWorld.getSpawnLocation());
		}
		
		// WARNING - unload world must have save true - otherwise it doesn't
		// release lock on world files immediately and can later cause world
		// corruption
		boolean success = Bukkit.unloadWorld(world, true);
		if (!success)
			Bukkit.getLogger().severe("Failed to unload world");
		
		try {
			File file = world.getWorldFolder();
			if (file.exists()) {
				FileUtils.deleteDirectory(file);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete world folder: ", e);
		}
		Bukkit.getLogger().info("Finished unloading map");
	}
	
	private String getNextWorldName() {
		worldIndex++;
		worldIndex = worldIndex % worlds.size();
		return worlds.get(worldIndex);
	}
	
	public File getNightfallConfig(World world) {
		return new File(world.getWorldFolder(), "nightfall.yml");
	}
}
