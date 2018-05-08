package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.Phase;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class AIManager {
	public static AIManager getManager() {
		return Game.getGame().getMonsterManager().getAiManager();
	}
	
	private int updateFreq;
	private final static double AI_MARK_DISTANCE = 4;
	
	
	private final Team aiTeam;
	private final BukkitRunnable runner;
	
	public AIManager() {
		runner = new BukkitRunnable() {
			@Override
			public void run() {
				updateAIs();
			}
		};
		
		String teamName = "ais";
		ChatColor teamColour = ChatColor.DARK_RED;
		
		aiTeam = Game.getGame().getNewTeam("ais");
		
		aiTeam.setPrefix(String.valueOf(teamColour));
		aiTeam.setDisplayName(teamColour + teamName);
		
		aiTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
		aiTeam.setCanSeeFriendlyInvisibles(true);
		aiTeam.setAllowFriendlyFire(false);
		this.maxAIs = 20;
		this.maxMarks = 10;
		this.updateFreq = 5*20;
	}
	
	public void start() {
		runner.runTaskTimer(NightfallPlugin.getPlugin(), updateFreq, updateFreq);
	}
	
	public void stop() {
		if (Game.getGame().getPhase().hasGameStarted())
			runner.cancel();
		removeAllAIs();
	}
	
	// ------ AI NAMES ------
	private final static Set<String> AI_NAMES = new HashSet<>();
	static {
		BufferedReader reader = new BufferedReader(new InputStreamReader(NightfallPlugin.getPlugin().getResource("ainames.txt")));
		String str;
		try {
			while ((str = reader.readLine()) != null) {
				AI_NAMES.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getRandomName() {
		return ChatColor.DARK_RED + Misc.getRandom(AI_NAMES);
	}
	
	// ------ SPAWN LOCATIONS ------
	private final Queue<AISpawnLocation> spawnSpots = new LinkedList<>();
	
	public boolean addAISpawnLocation(Location loc) {
		// Prevent spawning if spawn spot is too close to another
		for (AISpawnLocation spawnSpot : spawnSpots) {
			if (spawnSpot.isWithinRange(loc, AI_MARK_DISTANCE)) return false;
		}
		
		// Don't add in shrine
		if (GameMap.getCurrentMap().getCurrentShrineProtection().containsLocation(loc)) return false;
		
		// Only add if in air above solid ground
		Block block = loc.getBlock();
		Block above = block.getRelative(0, 1, 0);
		Block below = block.getRelative(0, -1, 0);
		Block twoBelow = block.getRelative(0, -2, 0);
		boolean validSpot =
				(
					!block.getType().isSolid() || !above.getType().isSolid()
				) && (
					below.getType().isSolid() || twoBelow.getType().isSolid()
				);
		
		if (!validSpot) return false;
		
		spawnSpots.add(new AISpawnLocation(loc));
		while (spawnSpots.size() > maxMarks)
			spawnSpots.remove();
		
		return true;
	}
	
	private double spawnChance = 0;
	private double multiplier = 1;
	private int maxAIs;
	private int maxMarks;
	private double maxMultiplier = 1;
	public double getBaseSpawnChance() { return spawnChance; }
	private void updateSpawnRates() {
		int dwarves = DwarfManager.getManager().getNumberOfPlayers();
		int mobs = MonsterManager.getManager().getNumberOfPlayers();
		
		maxAIs = 25 + mobs + 5 * dwarves;
		maxMarks = 50 + 5 * mobs + dwarves;
		
		maxAIs *= maxMultiplier;
		maxMarks *= maxMultiplier;
		
		double proportion = 1 - (double) ais.size()/ maxAIs;
		spawnChance = (0.1 + 0.025 * dwarves) * proportion * proportion;
		
		if (ais.size() >= maxAIs) {
			spawnChance = 0;
		}
		spawnChance *= multiplier;
	}
	
	public int getNumAIs() { return ais.size(); }
	public int getMaxAIs() { return maxAIs; }
	public int getNumMarks() { return spawnSpots.size(); }
	public int getMaxMarks() { return maxMarks; }
	
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
		updateSpawnRates();
	}
	public void setMaxMultiplier(double maxMultiplier) {
		this.maxMultiplier = maxMultiplier;
		updateSpawnRates();
	}
	
	// ------ ARE AIS SPAWNABLE ------
	private boolean aisSpawnable = true;
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		removeAllAIs();
		return aisSpawnable;
	}
	
	private boolean canAIsSpawn() {
		return aisSpawnable && Game.getGame().getPhase() == Phase.GAME && !DoomManager.getManager().isDoom();
	}
	
	// ------ AI MANAGEMENT ------
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Region shrineProt = GameMap.getCurrentMap().getCurrentShrineProtection();
		for (AIEntity ai : new HashSet<>(ais.values())) {
			ai.naturalUpdateTarget();
			
			if (shrineProt.continsGameEntity(ai))
				ai.remove();
			
			if (ai.isDead())
				unregisterAI(ai);
		}
		
		if (Game.getGame().getPhase() != Phase.GAME) return;
		
		// Try spawn more AIs
		if (canAIsSpawn()) {
			updateSpawnRates();
			
			Iterator<AISpawnLocation> spawnIterator = spawnSpots.iterator();
			while (spawnIterator.hasNext()) {
				AISpawnLocation spawnSpot = spawnIterator.next();
				if (spawnSpot.isValid()) {
					spawnSpot.update();
				} else {
					spawnIterator.remove();
				}
			}
		}
		
		// Update ai marks spots
		for (MonsterPlayer monster : MonsterManager.getManager().getAlivePlayerMobs()) {
			// If mob on ground or a bit above it
			Location location = monster.getLocation();
			boolean success = addAISpawnLocation(location);
			if (!success) continue;
			
			// Also add offset if previous was successfull - will be either -5, 0, +5 for x and z
			int xOffset = 5 * Misc.randomInt(-1,1);
			int zOffset = 5 * Misc.randomInt(-1,1);
			Location offset = location.clone().add(xOffset, 0, zOffset);
			addAISpawnLocation(offset);
		}
	}
	
	
	public void spawnAIs(AIType type, Location location, int number) {
		for (int i=0; i<number; i++) {
			spawnAI(type, location);
		}
	}
	
	public void spawnAIs(AIType type, Location location, Dwarf target, int number) {
		for (int i=0; i<number; i++) {
			spawnAI(type, location, target);
		}
	}
	
	public void spawnAI(AIType type, Location location) {
		spawnAI(type, location, null);
	}
	
	public void spawnAI(AIType type, Location location, Dwarf target) {
		AIEntity<?> ai = type.createAI(location, getRandomName(), target);
		aiTeam.addEntry(ai.getUniqueId().toString());
		ais.put(ai.getUniqueId(), ai);
	}
	
	public void registerAI(AIEntity<?> ai) {
		aiTeam.addEntry(ai.getUniqueId().toString());
		ais.put(ai.getUniqueId(), ai);
	}

	
	void unregisterAI(AIEntity entity) {
		ais.remove(entity.getUniqueId());
		aiTeam.removeEntry(entity.getUniqueId().toString());
	}
	
	
	public Collection<AIEntity> getAIs() {
		return ais.values();
	}
	public Collection<AIEntity> getRemoveableAIs() {
		return new HashSet<>(ais.values());
	}
	public AIEntity getAI(Entity entity) {
		return ais.get(entity.getUniqueId());
	}
	
	public void removeAllAIs() {
		for (AIEntity ai : getRemoveableAIs()) {
			ai.remove();
		}
		ais.clear();
	}
	
	public void clearArea(Location center, double range) {
		for (AIEntity entity : getRemoveableAIs()) {
			if (center.distance(entity.getLocation()) <= range)
				entity.remove();
		}
		
		spawnSpots.removeIf(location -> location.isWithinRange(center,range));
	}
}
