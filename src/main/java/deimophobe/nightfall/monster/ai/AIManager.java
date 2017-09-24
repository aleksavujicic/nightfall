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
import org.bukkit.Sound;
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
		
	private final static int BASE_MAX_AIS = 60;
	
	private final static int MAX_AI_MARKS = 40;
	private final static double AI_MARK_DISTANCE = 5;
	
	private final static int UPDATE_FREQ =  5*20;
	
	
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
	}
	
	public void start() {
		runner.runTaskTimer(NightfallPlugin.getPlugin(), UPDATE_FREQ, UPDATE_FREQ);
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
		// Prevent spawning if onSpawn spot is too close to another
		for (Location spawnSpot : spawnSpots) {
			if (loc.distance(spawnSpot) <= AI_MARK_DISTANCE)
				return;
		}
		
		spawnSpots.add(loc);
		while (spawnSpots.size() > MAX_AI_MARKS)
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
		
		// Try onSpawn more AIs
		if (aisSpawnable && Game.getGame().getPhase() == Phase.GAME && !DoomManager.getManager().isDoom()) {
			int dwarves = DwarfManager.getManager().getNumberOfPlayers();
			int mobs = MonsterManager.getManager().getNumberOfPlayers();
			
			double spawnChance = (10 + mobs + dwarves*4) * 0.008;
			spawnChance += (Game.getGame().isNight() ? 0.03 : 0);
			
			int maxAIs = BASE_MAX_AIS;
			maxAIs += (mobs + dwarves*2);
			
			Collection<Location> spotsToRemove = new HashSet<>();
			
			for (Location spawnSpot : spawnSpots) {
				//spawnSpot.getWorld().spawnParticle(Particle.HEART, spawnSpot, 1, 0, 0, 0);
				if (ais.size() >= maxAIs) break;
				
				double random = Math.random();
				if (random > 0.9) { // Try remove
					int count = 0;
					for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
						if (spawnSpot.distance(dwarf.getLocation()) <= 7) {
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
				if (closestDwarf == null) continue;
				
				// Create zombie with all right stuff
				AIEntity ai = new AIEntity(spawnSpot, getRandomName(), closestDwarf);
				aiTeam.addEntry(ai.getUniqueId().toString());
				ais.put(ai.getUniqueId(), ai);

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
				if (monster.getPlayer().isOnGround() || monster.getLocation().getBlock().getRelative(0,-2,0).getType().isSolid())
					addAISpawnLocation(monster.getLocation());
		}
	}
	
	
	public void spawnAIs(Location location, int num) {
		for (int i=0; i<num; i++)
			spawnAI(location);
	}
	
	public void spawnAI(Location location) {
		AIEntity ai = new AIEntity(location, getRandomName());
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
	public AIEntity getAI(Entity entity) {
		return ais.get(entity.getUniqueId());
	}
	
	public void removeAllAIs() {
		for (AIEntity ai : ais.values()) {
			ai.remove();
		}
		ais.clear();
	}
	
	public void clearArea(Location center, double range) {
		for (AIEntity entity : ais.values()) {
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
