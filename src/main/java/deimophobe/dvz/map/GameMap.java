package deimophobe.dvz.map;

import deimophobe.dvz.DvZPlugin;
import deimophobe.dvz.Game;
import deimophobe.dvz.Phase;
import deimophobe.dvz.map.feature.MapFeature;
import deimophobe.dvz.map.region.NullRegion;
import deimophobe.dvz.map.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Deimophobe on 1/07/17.
 */
public class GameMap {
	private final String name;
	
	private final Collection<MapFeature> features = new HashSet<>();
	
	private final List<Shrine> shrines;
	private int currentShrineIndex;
	
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
			throw new InvalidMapConfigException("Map config must have a name.");
		
		this.dwarfSpawn = getLocation(config, "dwarfspawn");
		if (dwarfSpawn == null)
			throw new InvalidMapConfigException("GameMap must specify a dwarf spawn.");
		
		this.lobby = getLocation(config, "lobby");
		if (lobby == null)
			throw new InvalidMapConfigException("Map config must specify a lobby.");
		
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
			@Override public Location getLocation() {return shrines.get(currentShrineIndex).getShrineCenter();}
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
		
		setWorldSettings();
		
		vault = config.getInt("gold", 1000);
		if (!config.contains("gold"))
			Bukkit.getLogger().warning("No starting gold specified - defaulting to 1000.");
		updateVault();
		
		shrineUpdater = new BukkitRunnable() {
			@Override
			public void run() {
				if (Game.getGame().getPhase() == Phase.END) {
					this.cancel();
				} else {
					shrines.get(currentShrineIndex).update();
				}
			}
		};
		
		// Add shrine features
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
	
	
	public void unload() {
		if (Game.getGame().getPhase() == Phase.GAME)
			shrineUpdater.cancel();
		
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
		switch (Game.getGame().getPhase()) {
			case BUILD:
			case PLAGUE:
				vault += 5;
				break;
			case GAME:
				vault += 2;
				break;
		}
		updateVault();
	}
	
	public boolean useGold(int amt) {
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
	
	private void updateGold() {
		Game.getGame().setGold(gold);
	}
	private void updateVault() {
		Game.getGame().setVault(vault);
	}
	
	
	// ~~~~~~~ SHRINE STUFF ~~~~~~~
	
	public void onMobRelease() {
		changeShrine();
		shrineUpdater.runTaskTimer(DvZPlugin.getPlugin(), 40, 40);
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
		}.runTaskLater(DvZPlugin.getPlugin(), newShrine.getSwapoverDelay());
		newShrine.onActive();
	}
	
	public void damageShrine(int damage) {
		shrines.get(currentShrineIndex).damageShrine(damage);
	}
	
	public void recoverShrine(int recovery) {
		shrines.get(currentShrineIndex).recoverShrine(recovery);
	}
	
	
	public void onEnd() {
		shrineUpdater.cancel();
		for (MapFeature feature : features) {
			feature.deactivate();
		}
	}
	
	
	// ~~~~ MISC ~~~~~
	
	public Location getLocation(ConfigurationSection section, String key) throws InvalidMapConfigException {
		if (!section.contains(key))
			return null;
		
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
