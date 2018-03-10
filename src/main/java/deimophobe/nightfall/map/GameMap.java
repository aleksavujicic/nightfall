package deimophobe.nightfall.map;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.blocks.blocktype.BlockType;
import deimophobe.nightfall.map.feature.FeatureCreator;
import deimophobe.nightfall.map.feature.MapFeature;
import deimophobe.nightfall.map.region.NullRegion;
import deimophobe.nightfall.map.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Created by Deimophobe on 1/07/17.
 */
public class GameMap {
	private final String name;
	public String getName() { return name; }
	
	private final Collection<MapFeature> features = new HashSet<>();
	
	private final List<Shrine> shrines;
	private int currentShrineIndex;
	private Game game;
	
	public int getNumShrines() {return shrines.size();}
	
	private Location currentMobSpawn;
	private Region currentMobProtection;
	private Region currentShrineProtection;
	private Region currentShrineRegion;
	public Location getCurrentMobspawn() {return currentMobSpawn;}
	public Region getCurrentMobProtection() {return currentMobProtection;}
	public Region getCurrentShrineProtection() {return currentShrineProtection;}
	public Region getCurrentShrineRegion() {return currentShrineRegion;}
	
	private final Location dwarfSpawn;
	private final Location lobby;
	public Location getDwarfSpawn() {return dwarfSpawn;}
	public Location getLobbySpawn() {return lobby;}
	
	private final Set<Region> unbreakableRegions = new HashSet<>();
	
	
	private final List<CompassLocation> compassLocations;
	public List<CompassLocation> getCompassLocations() {
		return compassLocations;
	}
	
	
	private final World world;
	public World getWorld() {
		return world;
	}
	
	
	private BukkitRunnable shrineUpdater;
	
	
	public static GameMap getCurrentMap() {
		return Game.getGame().getMap();
	}
	
	
	GameMap(World world) throws InvalidMapConfigException {
		this.world = world;
		
		File configFile = MapManager.getManager().getNightfallConfig(world);
		if (!configFile.exists())
			throw new InvalidMapConfigException("Config file (nightfall.yml) does not exist in map folder!");
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		
		this.name = config.getString("name");
		if (name == null)
			throw new InvalidMapConfigException("Map config must specify a name.", config, "name");
		
		this.dwarfSpawn = getLocation(config, "dwarfspawn");
		if (dwarfSpawn == null)
			throw new InvalidMapConfigException("GameMap must specify a dwarf spawn.", config, "dwarfspawn");
		
		this.lobby = getLocation(config, "lobby");
		if (lobby == null)
			throw new InvalidMapConfigException("Map config must specify a lobby.", config, "lobby");
		
		// Setup shrines
		shrines = new ArrayList<>();
		ConfigurationSection shrineConfig = config.getConfigurationSection("shrines");
		int shrineNum = 1;
		int shrineAmt = shrineConfig.getKeys(false).size();
		for (String key : shrineConfig.getKeys(false)) {
			if (shrineNum == shrineAmt) {
				shrines.add(new FinalShrine(this, shrineConfig.getConfigurationSection(key), shrineNum));
			} else {
				shrines.add(new Shrine(this, shrineConfig.getConfigurationSection(key), shrineNum));
			}
			shrineNum++;
		}
		if (shrines.size() == 0)
			throw new InvalidMapConfigException("Map config must have at least one shrine.");
		currentShrineIndex = -1;
		
		currentMobSpawn = shrines.get(0).getMobSpawn();
		currentMobProtection = new NullRegion();
		currentShrineProtection = new NullRegion();
		currentShrineRegion = new NullRegion();
		
		
		// Setup compass
		compassLocations = new ArrayList<>();
		
		// Add dwarf/mob spawn and current shrine
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {return getDwarfSpawn();}
			@Override public String getName() {return "Dwarf spawn";}
		});
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {
				if (currentShrineIndex == -1) return shrines.get(0).getShrineCenter();
				return shrines.get(currentShrineIndex).getShrineCenter();}
			@Override public String getName() {return "Current shrine";}
		});
		compassLocations.add(new CompassLocation() {
			@Override public Location getLocation() {return getCurrentMobspawn();}
			@Override public String getName() {return "Current mob spawn";}
		});
		
		ConfigurationSection compassConfig = config.getConfigurationSection("compass");
		if (compassConfig != null) {
			for (String key : compassConfig.getKeys(false)) {
				Location location = getLocation(compassConfig, key);
				compassLocations.add(new FixedCompassLocation(key, location));
			}
		} else {
			Bukkit.getLogger().warning("No compass section found.");
		}
		
		vault = config.getInt("gold", 1000);
		if (!config.contains("gold"))
			Bukkit.getLogger().warning("No starting gold specified - defaulting to 1000.");
		
		// Add shrine features
		if (config.contains("features")) {
			ConfigurationSection featSection = config.getConfigurationSection("features");
			for (String key : featSection.getKeys(false)) {
				features.add(FeatureCreator.createFeature(this, key, featSection.getConfigurationSection(key)));
			}
		}
		
		shrineUpdater = new BukkitRunnable() {
			@Override
			public void run() {
				if (game.getPhase() == Phase.END) {
					this.cancel();
				} else {
					shrines.get(currentShrineIndex).update();
				}
			}
		};
	}
	
	public void setupGame(Game game) {
		this.game = game;
		updateVault();
	}
	
	public void addUnbreakableRegion(Region region) {
		unbreakableRegions.add(region);
	}
	
	public boolean isBlockBreakable(Block block) {
		if (block == null) return false;
		
		if (BlockType.ALWAYS_BREAKABLE.matchesBlock(block))
			return true;
		
		if (BlockType.UNBREAKABLE_BLOCKS.matchesBlock(block))
			return false;
		
		for (Region region : unbreakableRegions) {
			if (region.containsBlock(block))
				return false;
		}
		
		return true;
	}
	
	public boolean isBlockPlaceable(Block block) {
		if (block == null) return false;
		
		if (BlockType.ALWAYS_BREAKABLE.matchesBlock(block))
			return true;
		
		if (BlockType.UNPLACEABLE_BLOCKS.matchesBlock(block))
			return false;
		
		for (Region region : unbreakableRegions) {
			if (region.containsBlock(block))
				return false;
		}
		
		return true;
	}
	
	
	public void unload() {
		if (game.getPhase() == Phase.GAME)
			shrineUpdater.cancel();
		
		for (MapFeature feature : features) {
			feature.deactivate();
		}
		
		MapManager.getManager().unloadAndDeleteWorld(world);
	}
	
	// ------ GOLD ------
	private int gold;
	private int vault;
	public int getGold() {
		return gold;
	}
	
	public boolean hasGold() {
		return gold != 0;
	}
	
	public void mineGold() {
		int random1 = (int)(Math.random() * 2);
		int random2 = (int)(Math.random() * 2);
		switch (game.getPhase()) {
			case BUILD:
			case PLAGUE:
				vault += 4 + random1 + random2;
				break;
			case GAME:
				vault += 2 + random1;
				break;
		}
		updateVault();
	}
	
	public boolean tryUseGold(int amt) {
		if (amt < 0)
			throw new IllegalArgumentException("Using less than 0 gold. Amount: " + amt);
		
		if (gold >= amt) {
			gold -= amt;
			updateGold();
			return true;
		} else {
			return false;
		}
	}
	
	public void stealGold(int amt) {
		if (amt < 0)
			throw new IllegalArgumentException("Stealing less than 0 gold. Amount: " + amt);
		
		gold -= amt;
		if (gold < 0) gold = 0;
		
		updateGold();
	}

	public void addGold(int amt) {
		gold += amt;
		if (gold < 0) {
			gold = 0;
		}
		updateGold();
	}

	public void addVaultGold(int amt) {
		vault += amt;
		if (vault < 0) {
			vault = 0;
		}
		updateVault();
	}

	private void updateGold() {
		game.setGold(gold);
	}
	private void updateVault() {
		game.setVault(vault);
	}
	
	
	// ~~~~~~~ SHRINE STUFF ~~~~~~~
	
	public void onMobRelease() {
		changeShrine();
		shrineUpdater.runTaskTimer(NightfallPlugin.getPlugin(), 40, 40);
	}
	
	public void changeShrine() {
		currentShrineIndex++;
		Shrine newShrine = shrines.get(currentShrineIndex);
		
		// Split gold
		double weight = newShrine.getGoldWeight();
		gold = (int) (weight * vault);
		vault -= gold;
		updateGold();
		updateVault();
		
		currentShrineProtection = newShrine.getShrineProtection();
		currentShrineRegion = newShrine.getShrineRegion();
		new BukkitRunnable() {
			@Override public void run() {
				currentMobSpawn = newShrine.getMobSpawn();
				currentMobProtection = newShrine.getMobProtection();
			}
		}.runTaskLater(NightfallPlugin.getPlugin(), newShrine.getSwapoverDelay()*20);
		newShrine.onActive();
	}
	
	public void damageShrine(int damage) {
		shrines.get(currentShrineIndex).damageShrine(damage);
	}
	
	public void recoverShrine(int recovery) {
		shrines.get(currentShrineIndex).recoverShrine(recovery);
	}

	public int getCurrentShrineIndex() { return currentShrineIndex;}
	
	public void onEnd() {
		shrineUpdater.cancel();
	}
	
	public void forceSetMobspawn(Location location) {
		currentMobSpawn = location.clone();
	}
	
	
	// ~~~~ MISC ~~~~~
	
	public Location getLocation(ConfigurationSection section, String key) throws InvalidMapConfigException {
		if (!section.contains(key))
			throw new InvalidMapConfigException("Map config does not contain location key: '"+key+"'", section);
		
		List<Double> doubleList = section.getDoubleList(key);
		
		if (world == null)
			throw new IllegalStateException("World has not been initialised - cannot create location.");
		
		switch (doubleList.size()) {
			case 3:
				return new Location(world, doubleList.get(0), doubleList.get(1) ,doubleList.get(2));
			case 4:
				return new Location(world, doubleList.get(0), doubleList.get(1), doubleList.get(2),  (float) doubleList.get(3).doubleValue(), 0f);
			case 5:
				return new Location(world, doubleList.get(0), doubleList.get(1), doubleList.get(2),  (float) doubleList.get(3).doubleValue(), (float) doubleList.get(4).doubleValue());
			
			default:
				throw new InvalidMapConfigException(
						"Double lists for location '"+section.getCurrentPath() + "." + key + "' " +
								"must have 3, 4, or 5 elements - not " + doubleList.size() + "."
				);
		}
	}
}
