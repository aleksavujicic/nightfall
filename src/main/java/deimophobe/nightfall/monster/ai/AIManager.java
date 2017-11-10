package deimophobe.nightfall.monster.ai;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.Game;
import deimophobe.nightfall.Misc;
import deimophobe.nightfall.Phase;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.region.Region;
import deimophobe.nightfall.monster.MonsterManager;
import deimophobe.nightfall.monster.MonsterPlayer;
import deimophobe.nightfall.monster.doom.DoomManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.Math.floor;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class AIManager {
	public static AIManager getManager() {
		return Game.getGame().getMonsterManager().getAiManager();
	}
		
	private int maxAIs;
	private int maxMarks;
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
		
		aiTeam = Misc.getNewTeam("ais");
		
		aiTeam.setPrefix(String.valueOf(teamColour));
		aiTeam.setDisplayName(teamColour + teamName);
		
		aiTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
		aiTeam.setCanSeeFriendlyInvisibles(true);
		aiTeam.setAllowFriendlyFire(false);
		this.maxAIs = 20;
		this.maxMarks = 10;
		this.updateFreq = 4*20;
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
	private String getRandomName() {
		return ChatColor.DARK_RED + Misc.getRandom(AI_NAMES);
	}
	
	// ------ SPAWN LOCATIONS ------
	private final Queue<Location> spawnSpots = new LinkedList<>();
	
	private void addAISpawnLocation(Location loc) {
		// Prevent spawning if spawn spot is too close to another
		for (Location spawnSpot : spawnSpots) {
			if (loc.distance(spawnSpot) <= AI_MARK_DISTANCE)
				return;
		}
		
		spawnSpots.add(loc);
		while (spawnSpots.size() > maxMarks)
			spawnSpots.remove();
	}
	
	// ------ ARE AIS SPAWNABLE ------
	private boolean aisSpawnable = true;
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		removeAllAIs();
		return aisSpawnable;
	}
	
	
	// ------ AI MANAGEMENT ------
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Region shrineProt = GameMap.getCurrentMap().getCurrentShrineProtection();
		for (AIEntity ai : new HashSet<>(ais.values())) {
			ai.updateTarget();
			
			if (shrineProt.continsGameEntity(ai))
				ai.remove();
			
			if (ai.isDead())
				unregisterAI(ai);
		}
		
		if (Game.getGame().getPhase() != Phase.GAME)
			return;
		
		// Try spawn more AIs
		if (aisSpawnable && Game.getGame().getPhase() == Phase.GAME && !DoomManager.getManager().isDoom()) {
			int dwarves = DwarfManager.getManager().getNumberOfPlayers();
			int mobs = MonsterManager.getManager().getNumberOfPlayers();

			double proportion = (maxAIs - ais.size()) / maxAIs;
			double spawnChance = (0.2 + 0.01 * dwarves) * proportion * proportion;
			spawnChance *= (Game.getGame().isNight() ? 1.2 : 1);
			
			maxAIs = 20 + 2 * mobs + 10 * dwarves;
			maxMarks = 10 + 2 * mobs + 10 * dwarves;
			
			Collection<Location> spotsToRemove = new HashSet<>();
			
			for (Location spawnSpot : spawnSpots) {
				//spawnSpot.getWorld().spawnParticle(Particle.HEART, spawnSpot, 1, 0, 0, 0);
				if (ais.size() >= maxAIs) break;
				
				double random = Math.random();
				if (random < 0.2) { // Try remove if too close to dwarves
					int count = 0;
					for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
						if (spawnSpot.distance(dwarf.getLocation()) <= 5) {
							count++;
						}
					}
					if (count >= 2)
						spotsToRemove.add(spawnSpot);
					continue;
				}
				if (random > spawnChance) continue;
				if (shrineProt.containsLocation(spawnSpot)) {
					spotsToRemove.add(spawnSpot);
					continue;
				}
				
				// Find closest dwarf and set as target. If no such dwarf, dont spawn.
				double leastDistance = 25;
				Dwarf closestDwarf = null;
				for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
					double dist = spawnSpot.distance(dwarf.getLocation());
					if (dist <= leastDistance) {
						leastDistance = dist;
						closestDwarf = dwarf;
					}
				}
				if (closestDwarf == null) {
					if (Math.random() < 0.2) {
						spotsToRemove.add(spawnSpot); // If there's no dwarf to spawn on then slowly phase the spawnspots out
					}
					continue;
				}

				// Create zombie with all right stuff
				spawnAIs(spawnSpot, closestDwarf, 3);

				// Destroy spawnspots after average of 3 AI spawns
				if (Math.random() < 0.333) {
					spotsToRemove.add(spawnSpot);
				}
			}
			
			for (Location toRemove : spotsToRemove) {
				spawnSpots.remove(toRemove);
			}
		}
		
		// Update ai marks spots
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			if (monster.isAlive())
				// If mob on ground or a bit above it
				if (monster.getPlayer().isOnGround() || monster.getLocation().getBlock().getRelative(0,-2,0).getType().isSolid()) {
					addAISpawnLocation(monster.getLocation());
					int xOffset = 4 * (int)Math.floor(Math.random() * 3 - 1);
					int zOffset = 4 * (int)Math.floor(Math.random() * 3 - 1);
					Block nearby = monster.getLocation().getBlock().getRelative(xOffset,-2,zOffset);
					Block atSpawnPoint = monster.getLocation().getBlock().getRelative(xOffset,0,zOffset);
					if (nearby.getType().isSolid() && atSpawnPoint.getType() == Material.AIR) {
						addAISpawnLocation(monster.getLocation().add(xOffset, 0, zOffset));
					}
				}
		}
	}
	
	
	public void spawnAIs(Location location, int num) {
		for (int i=0; i<num; i++)
			spawnAI(location);
	}

	public void spawnAIs(Location location, Dwarf target, int num) {
		for (int i=0; i<num; i++)
			spawnAI(location, target);
	}

	public void spawnAI(Location location) {
		AIEntity ai = new AIZombie(location, getRandomName());
		aiTeam.addEntry(ai.getUniqueId().toString());
		ais.put(ai.getUniqueId(), ai);
	}
	
	public void spawnAI(Location location, Dwarf target) {
		AIEntity ai = new AIZombie(location, getRandomName(), target);
		aiTeam.addEntry(ai.getUniqueId().toString());
		ais.put(ai.getUniqueId(), ai);
	}

	public void spawnAISkeleton(Location location) {
		AIEntity ai = new AIFireSkeleton(location, getRandomName());
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
		
		spawnSpots.removeIf(location -> center.distance(location) <= range);
	}
	
	static {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(NightfallPlugin.getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacket().getSoundEffects().read(0) == Sound.ENTITY_ZOMBIE_DEATH) {
					event.setCancelled(true);
				}
			}
		});
	}
}
