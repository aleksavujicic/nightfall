package deimophobe.nightfall.map;

import com.google.common.base.Preconditions;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.VoidChunkGenerator;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.util.WeightedSet;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by Deimophobe on 17/03/17.
 */
public class MapManager {
	private static final MapManager manager = new MapManager();
	public static MapManager getManager() {
		return manager;
	}
	
	private final List<String> worlds = new ArrayList<>(Arrays.asList("Nightfall1","Nightfall2","Nightfall3"));
	private int worldIndex = 0;
	
	private final File mapConfigFile;
	private final File mapWorldFolder;
	
	private final Deque<MapWorld> mapQueue = new LinkedList<>();
	
	private final Map<String, MapWorld> maps = new HashMap<>();
	private boolean autocycle;
	private int cycleTime;
	
	private MapManager() {
		// Save default config
		World defaultWorld = Bukkit.getWorlds().get(0);
		File configFile = getNightfallConfig(defaultWorld);
		InputStream is = NightfallPlugin.getPlugin().getResource("default-map.yml");
		try {
			Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			NightfallPlugin.logger().severe("Failed to save default config!");
			e.printStackTrace();
		}
		
		
		// Map config files and folders
		mapConfigFile = new File(NightfallPlugin.getPlugin().getDataFolder(), "maps.yml");
		mapWorldFolder = new File(Bukkit.getWorldContainer(), "maps");
		
		if (!mapConfigFile.exists()) {
			NightfallPlugin.getPlugin().saveResource("maps.yml", false);
			NightfallPlugin.logger().warning("No maps.yml file found - creating default. This may not have the neccesary maps.");
		}
		
		if (!mapWorldFolder.exists()) {
			NightfallPlugin.logger().warning("No maps folder found - creating empty folder. This has no maps and will disable map loading.");
			boolean success = mapWorldFolder.mkdir();
			if (!success) {
				NightfallPlugin.logger().severe("Failed to create map folder!?");
			}
		}
		
		
		// Load config
		reloadConfig();
		
		// Check maps exist
		if (maps.isEmpty()) {
			NightfallPlugin.logger().severe("No maps were found. Disabling map loading.");
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
	
	public void reloadConfig() {
		maps.clear();
		mapQueue.clear();
		
		final Logger logger = NightfallPlugin.logger();
		logger.info("Reloading map config.");
		Configuration mapsConfig = YamlConfiguration.loadConfiguration(mapConfigFile);
		ConfigurationSection mapSection = mapsConfig.getConfigurationSection("maps");
		
		autocycle = mapsConfig.getBoolean("auto-cycle", true);
		cycleTime = mapsConfig.getInt("cycle-time", 30);
		
		if (cycleTime <= 0) {
			logger.severe("Cycle time should be positive.");
			cycleTime = 30;
		}
				
		
		if (!mapWorldFolder.exists()) {
			logger.severe("No map folder found - no maps will be created.");
			return;
		}

		if (mapSection == null) {
			logger.severe("No section found for maps in maps.yml - no maps will be created.");
			return;
		}
		
		for (String mapName : mapSection.getKeys(false)) {
			// Get config section.
			ConfigurationSection mapConfig = mapSection.getConfigurationSection(mapName);
			if (mapConfig == null) {
				logger.severe("Map with key '" + mapName +"' has invalid format in maps.yml.");
				continue;
			}
			
			// Get folder location and check it exists
			String mapFilename = mapConfig.getString("folder");
			if (mapFilename == null) {
				logger.severe("No map folder given for key '" + mapName +"' in maps.yml.");
				continue;
			}
			File mapFile = new File(mapWorldFolder, mapFilename);
			if (!mapFile.exists()) {
				logger.severe("No map found in map folder with name '" + mapFilename +"' in maps.yml.");
				continue;
			}
			
			// Get rotation
			String rotationString = mapConfig.getString("rotation");
			MapWorld.MapRotation rotation;
			if (rotationString == null) {
				rotation = MapWorld.MapRotation.DISABLED;
				logger.warning("Map " + mapName + " has no rotation specified - assuming disabled.");
			} else {
				try {
					rotation = Misc.getEnumMemberFromString(rotationString, MapWorld.MapRotation.values(), "map");
				} catch (UnknownEnumElementException e) {
					rotation = MapWorld.MapRotation.DISABLED;
					logger.severe("Map " + mapName + " has unknown rotation '" + rotationString +"'. Setting to disabled.");
				}
			}
			
			MapWorld mapWorld = new MapWorld(mapName, mapFile, rotation);
			maps.put(mapName, mapWorld);
			
		}
	}
	
	// ~~~~~ MAP QUEUEING ~~~~~
	
	/** Places map at tail of queue if valid map. */
	public void enqueueMap(MapWorld map) {
		mapQueue.add(map);
	}
	
	/** Places map at head of queue if valid map. */
	public void insertMap(MapWorld map) {
		mapQueue.addFirst(map);
	}
	
	public List<MapWorld> getMapQueue() {
		return new ArrayList<>(mapQueue);
	}
	
	public void clearMapQueue() {
		mapQueue.clear();
	}
	
	public MapWorld peekMap() {
		return mapQueue.peek();
	}
	
	public void enqueueRandomMapIfEmpty() {
		if (!mapQueue.isEmpty()) return;
		
		MapWorld map = getRandomActiveMap();
		enqueueMap(map);
	}
	
	public Set<String> getMapNames() {
		return maps.keySet();
	}
	
	public Collection<MapWorld> getMaps() { return maps.values(); }
	
	public MapWorld getMap(@NotNull String name) {
		checkNotNull(name);
		checkArgument(maps.containsKey(name), "%s is not a valid map name", name);
		
		return maps.get(name);
	}
	
	public MapWorld tryGetMap(@NotNull String name) {
		return maps.get(name);
	}
	
	private MapWorld getRandomActiveMap() {
		// Get set of potential maps
		Set<MapWorld> mapSet = new HashSet<>(maps.values());
		mapSet.removeIf(map -> map.getRotation() == MapWorld.MapRotation.DISABLED);
		
		if (mapSet.size() == 0) throw new IllegalStateException("No active maps to load");
		if (mapSet.size() == 1) return Misc.getRandom(mapSet);
		
		if (Game.getGame() != null) {
			GameMap currentMap = GameMap.getCurrentMap();
			if (currentMap != null) {
				String current = currentMap.getID();
				mapSet.removeIf(map -> map.getName().equalsIgnoreCase(current));
			}
		}
		
		WeightedSet<MapWorld> weightedMaps = new WeightedSet<>(mapSet);
		return weightedMaps.getRandom();
	}
	
	// ~~~~~ MAP LOADING ~~~~~
	
	public GameMap loadNextMap() {
		// Load normal world if disabled
		if (!enabled) {
			NightfallPlugin.logger().warning("Map loading disabled, loading default map.");
			return loadDefaultMap();
		}
		
		MapWorld nextMap = mapQueue.poll();
		GameMap map;
		if (nextMap == null) {
			map = loadRandomMap();
		} else {
			map = loadMap(nextMap);
		}
		return map;
	}
	
	private GameMap loadDefaultMap() {
		World world = Bukkit.getWorlds().get(0);
		try {
			return new GameMap("default", world);
		} catch (InvalidMapConfigException e) {
			throw new RuntimeException("Default map config is invalid, can't start game.", e);
		}
	}
	
	private GameMap loadRandomMap() {
		MapWorld map = getRandomActiveMap();
		return loadMap(map);
	}
	
	private GameMap loadMap(MapWorld map) {
		String name = map.getName();
		NightfallPlugin.logger().info("Begin loading map " + name);
		// Don't do anything if disabled
		checkState(enabled, "Attempted to load map while map loading is disabled.");
		
		File mapFolder = maps.get(name).getWorldLocation();
		
		World world = null;
		try {
			world = createMapWorld(mapFolder);
			return new GameMap(name, world);
		} catch (MapLoadingException | InvalidMapConfigException e) {
			e.printStackTrace();
			unloadAndDeleteWorld(world);
			return loadDefaultMap();
		} finally {
			NightfallPlugin.logger().info("Finished loading map " + name);
		}
	}
	
	private World createMapWorld(File mapFolder) throws MapLoadingException {
		NightfallPlugin.logger().info("Begin creation of map world: " + mapFolder.toString());
		
		String worldFilename = getNextWorldName();
		
		// Figure out stored map folder and folder of world to play on.
		File worldFolder = new File(Bukkit.getWorldContainer(), worldFilename);
		
		// If map folder is empty
		if (!mapFolder.exists())
			throw new MapLoadingException("Game map folder " + mapFolder.toString() + " does not exist");
		
		// Delete world folder if it exists
		try {
			if (worldFolder.exists()) {
				NightfallPlugin.logger().warning("World folder '"+worldFilename+"' exists!");
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
		
		if (uidFile.exists()) throw new MapLoadingException("Failed to delete uid file.");
		if (lockFile.exists()) throw new MapLoadingException("Failed to delete lock file.");
		
		// Figure out the environment. This is a bit of a hack as ideally the nightfall.yml file should only be loaded once
		File configFile = new File(worldFolder, "nightfall.yml");
		if (!configFile.exists()) throw new MapLoadingException("Config file (nightfall.yml) does not exist in map folder!");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		String envName = config.getString("environment", "normal");
		World.Environment environment;
		try {
			 environment = Misc.getEnumMemberFromString(envName, World.Environment.values(), "environment");
		} catch (UnknownEnumElementException e) {
			throw new MapLoadingException(e);
		}
		
		WorldCreator wc = new WorldCreator(worldFilename);
		wc.environment(environment);
		wc.generator(new VoidChunkGenerator());
		wc.generateStructures(false);
		
		World world = Bukkit.createWorld(wc);
		setDefaultWorldSettings(world);
		NightfallPlugin.logger().info("Finished creating world.");
		return world;
	}
	
	void unloadAndDeleteWorld(World world) {
		if (world == null) {
			NightfallPlugin.logger().severe("Cannot unload null world");
			return;
		}
		
		NightfallPlugin.logger().info("Begin unloading map.");
		if (world == getDefaultWorld()) {
			NightfallPlugin.logger().warning("Cannot unload default world (this is normal if map loading is disabled).");
			return;
		}
		
		// TP everyone away
		World safeWorld = getSafeWorld(world);
		for (Player player : world.getPlayers()) {
			if (player.isDead())
				player.spigot().respawn();
			player.teleport(safeWorld.getSpawnLocation());
		}
		
		// WARNING - unload world must have save true - otherwise it doesn't
		// release lock on world files immediately and can later cause world
		// corruption
		boolean success = Bukkit.unloadWorld(world, true);
		if (!success) {
			NightfallPlugin.logger().severe("Failed to unload world");
			throw new IllegalStateException("Failed to unload world " + world.getName());
		}
		
		try {
			File file = world.getWorldFolder();
			if (file.exists()) {
				FileUtils.deleteDirectory(file);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to delete world folder: ", e);
		}
		NightfallPlugin.logger().info("Finished unloading map");
	}
	
	private World getSafeWorld(World unsafe) {
		GameMap map = GameMap.getCurrentMap();
		if (map == null)
			return getDefaultWorld();
		
		World gameWorld = map.getWorld();
		if (gameWorld == null) {
			NightfallPlugin.logger().severe("Game world is null!?");
			return getDefaultWorld();
		}
		
		if (unsafe == gameWorld) {
			return getDefaultWorld();
		}

		return gameWorld;
	}
	
	private World getDefaultWorld() {
		return Bukkit.getWorlds().get(0);
	}
	
	private String getNextWorldName() {
		worldIndex++;
		worldIndex = worldIndex % worlds.size();
		return worlds.get(worldIndex);
	}
	
	
	// ~~~~~ MISC ~~~~~
	
	public File getNightfallConfig(World world) {
		return new File(world.getWorldFolder(), "nightfall.yml");
	}
	
	public void scheduleNewGame() {
		if (!autocycle) return;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				Game.createNewGame();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), cycleTime*20);
	}
	
	private void setDefaultWorldSettings(World world) {
		world.setTime(0);
		world.setAutoSave(false);
		world.setDifficulty(Difficulty.NORMAL);
		world.setKeepSpawnInMemory(false);
		world.setSpawnFlags(false, false);
		
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
		world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, true);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, false);
		world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, -1);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
		world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
		world.setGameRule(GameRule.RANDOM_TICK_SPEED, -1);
	}
}
