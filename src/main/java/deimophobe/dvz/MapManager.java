package deimophobe.dvz;

import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.shrine.ShrineManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
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
	
	private MapManager() {}
	
	
	private boolean enabled;
	public boolean isEnabled() {
		return enabled;
	}
	public void setMapsEnabled(boolean enabled) throws IOException {
		YamlConfiguration mapConfig = YamlConfiguration.loadConfiguration(mapsConfigFile);
		mapConfig.set("enabled", enabled);
		mapConfig.save(mapsConfigFile);
	}
	
	private final List<String> worlds = new ArrayList<>();
	private int worldIndex = 0;
	
	private final Set<String> maps = new HashSet<>();
	
	private File mapConfigFolder;
	private File mapWorldFolder;
	
	private File mapsConfigFile;
	
	private boolean loading = false;
	
	public void setup() {
		// Load config file
		Game.getGame().getPlugin().saveResource("maps.yml", false);
		mapsConfigFile = new File(Game.getGame().getPlugin().getDataFolder(), "maps.yml");
		Configuration mapConfig = YamlConfiguration.loadConfiguration(mapsConfigFile);
		
		// Add all game worlds - these are the worlds that the server will actually run on
		worlds.addAll(mapConfig.getStringList("worlds"));
		
		// If map loading is enabled (disable for quick reloads.
		enabled = mapConfig.getBoolean("enabled", true);
		
		// Map config and world folders
		mapConfigFolder = new File(Game.getGame().getPlugin().getDataFolder(), "maps");
		mapWorldFolder = new File(Bukkit.getWorldContainer(), "maps");
		
		// Find all maps in config folder and add them to list of maps
		if (mapConfigFolder.listFiles() != null) {
			for (File file : mapConfigFolder.listFiles()) {
				String name = file.getName();
				if (FilenameUtils.isExtension(name, "yml")) {
					maps.add(FilenameUtils.getBaseName(name));
				}
			}
		}
		// Delete any worlds left from previous sessions
		deleteAllGameWorlds();
		
		// Load normal world if disabled
		if (!enabled) {
			world = Bukkit.getWorlds().get(0);
			
			Bukkit.getLogger().warning("Map loading is disabled. Default world: " + world.getName() );
			Game.getGame().resetManagers();
			setupGameStuff(mapConfig.getConfigurationSection("default-map"));
		}
	}
	
	public boolean isMap(String map) {
		return maps.contains(map);
	}
	
	public Set<String> getMaps() {
		return maps;
	}
	
	public void loadRandomMap() {
		String map = Misc.getRandom(maps);
		loadMap(map);
	}
	
	public void loadMap(String map) {
		// Don't do anythin if disabled
		if (!enabled) return;
		
		if (loading) throw new IllegalStateException("Attempted to load another map while loading");
		loading = true;
		Bukkit.getLogger().info("Begin loading of map: "+map);
		
		// Get config file
		File configFile = new File(mapConfigFolder, map+".yml");
		
		if (!configFile.exists())
			throw new IllegalArgumentException("Unknown map: " + map);
		
		// Create config
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		
		// Get world names
		String mapWorld = config.getString("world");
		String gameWorld = getNextWorld();
		
		// Figure out stored map folder and folder of world to play on.
		File mapFolder = new File(mapWorldFolder, mapWorld);
		File gameFolder = new File(Bukkit.getWorldContainer(), gameWorld);
		
		
		// If map folder is empty
		if (!mapFolder.exists())
			throw new IllegalArgumentException("Map: " + map + " contains no world called: " + mapWorld);
		
		// Delete world folder if it exists
		deleteWorld(gameFolder);
		
		// Copy map over
		try {
			FileUtils.copyDirectory(mapFolder, gameFolder);
			File uidFile = new File(gameFolder, "uid.dat");
			uidFile.delete();
			File lockFile = new File(gameFolder, "session.lock");
			lockFile.delete();
		} catch (IOException e) {
			Bukkit.getLogger().severe("Failed to copy map " + mapFolder.getName() + " to world" + gameFolder.getName());
		}
		
		// Reset everything
		Game.getGame().resetManagers();
		
		// Load world
		World newWorld = Bukkit.createWorld(new WorldCreator(gameWorld));
		
		// The ol' switcheroo
		World oldWorld = world;
		world = newWorld;
		
		// Setup game things
		setupGameStuff(config);
		
		
		// Unload and delete old world
		if (oldWorld != null) {
			boolean success = Bukkit.unloadWorld(oldWorld, false);
			if (!success)
				Bukkit.getLogger().severe("Failed to unload world: " + oldWorld);
			
			new BukkitRunnable() {
				@Override
				public void run() {
					// Delete old world
					File oldFolder = oldWorld.getWorldFolder();
					deleteWorld(oldFolder);
					
					// Allow to load again
					Bukkit.getLogger().info("Finished loading map: "+map);
					loading = false;
				}
			}.runTaskLater(Game.getGame().getPlugin(), 60);
		} else {
			// Allow to load again
			Bukkit.getLogger().info("Finished loading map: "+map);
			loading = false;
		}
	}
	
	private String getNextWorld() {
		worldIndex++;
		worldIndex = worldIndex % worlds.size();
		return worlds.get(worldIndex);
	}
	
	public void deleteAllGameWorlds() {
		for (String world : worlds) {
			deleteWorld(world);
		}
	}
	
	private void deleteWorld(String worldName) {
		deleteWorld(new File(Game.getGame().getPlugin().getDataFolder(), worldName));
	}
	
	private void deleteWorld(File worldFile) {
		if (worldFile.exists()) {
			try {
				FileUtils.deleteDirectory(worldFile);
			} catch (IOException e) {
				Bukkit.getLogger().severe("Failed to delete world folder " + worldFile.getName());
			}
		}
	}
	
	
	
	
	private World world;
	public World getWorld() {
		return world;
	}
	
	private void setupGameStuff(ConfigurationSection mapConfig) {
		setWorldSettings();
		
		ShrineManager.getManager().setupManager(mapConfig);
		DwarfManager.getManager().setupManager();
		MonsterManager.getManager().setupManager();
		
		Game.getGame().startLobby();
	}
	
	private void setWorldSettings() {
		world.setTime(0);
		world.setAutoSave(false);
		world.setDifficulty(Difficulty.NORMAL);
		world.setKeepSpawnInMemory(false);
		world.setSpawnFlags(false, false);
		
		world.setGameRuleValue("announceAdvancements", "false");
		world.setGameRuleValue("doDaylightCycle", "true");
		world.setGameRuleValue("doEntityDrops", "false");
		world.setGameRuleValue("doFireTick", "true");
		world.setGameRuleValue("doMobLoot", "false");
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doTileDrops", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("keepInventory", "false");
		world.setGameRuleValue("maxEntityCramming", "-1");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("naturalRegeneration", "false");
		world.setGameRuleValue("showDeathMessages", "true");
		world.setGameRuleValue("spectatorsGenerateChunks", "false");
		world.setGameRuleValue("randomTickSpeed", "1");
	}
}
