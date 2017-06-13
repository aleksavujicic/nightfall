package deimophobe.dvz.monster.ai;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import deimophobe.dvz.Game;
import deimophobe.dvz.MapManager;
import deimophobe.dvz.Misc;
import deimophobe.dvz.Phase;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
import deimophobe.dvz.monster.doom.DoomManager;
import deimophobe.dvz.shrine.region.Region;
import deimophobe.dvz.shrine.ShrineManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;

/**
 * Created by Deimophobe on 27/01/17.
 */
public class AIManager {
	private static AIManager manager = new AIManager();
	public static AIManager getManager() {
		return manager;
	}
	
	private AIManager() {}
	
	private final static int MAX_AIS = 60;
	private final static double AI_SPAWN_CHANCE = 0.15;
	
	private final static int UPDATE_FREQ =  3*20;
	
	
	private BukkitRunnable runner;
	public void setup() {
		runner = new BukkitRunnable() {
			@Override
			public void run() {
				updateAIs();
			}
		};
		runner.runTaskTimer(Game.getGame().getPlugin(), UPDATE_FREQ, UPDATE_FREQ);
		setupTeam();
	}
	
	public void reset() {
		if (runner != null)
			runner.cancel();
		removeAllAIs();
		manager = new AIManager();
	}
	
	// ------ AI NAMES ------
	private final static Set<String> AI_NAMES = new HashSet<>();
	static {
		BufferedReader reader = new BufferedReader(new InputStreamReader(Game.getGame().getPlugin().getResource("ainames.txt")));
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
	
	private final static int MAX_AI_MARKS = 60;
	private final static double SPAWN_THRESHOLD = 5;
	
	private void addAISpawnLocation(Location loc) {
		// Prevent spawning if spawn spot is too close to another
		for (Location spawnSpot : spawnSpots) {
			if (loc.distance(spawnSpot) <= SPAWN_THRESHOLD)
				return;
		}
		
		spawnSpots.add(loc);
		while (spawnSpots.size() > MAX_AI_MARKS)
			spawnSpots.remove();
	}
	
	// ------ ARE AIS SPAWNABLE ------
	private boolean aisSpawnable = true;
	private boolean canSpawnAI(Location spawnSpot) { // TODO: do a break if one of the first few are fulfilled.
		return  (aisSpawnable &&
				Game.getGame().getPhase() == Phase.GAME &&
				!DoomManager.getManager().isDoom() &&
				ais.size() < MAX_AIS &&
				Math.random() < AI_SPAWN_CHANCE &&
				!ShrineManager.getManager().getShrine().getShrineProtection().containsLocation(spawnSpot));
	}
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		removeAllAIs();
		return aisSpawnable;
	}
	
	
	// ------ AI MANAGEMENT ------
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Region shrineProt = ShrineManager.getManager().getShrine().getShrineProtection();
		Set<UUID> deadAIs = new HashSet<>();
		for (AIEntity ai : ais.values()) {
			ai.updateTarget();
			
			if (ai.isDead())
				deadAIs.add(ai.getUniqueId());
		}
		for (UUID uuid : deadAIs)
			ais.remove(uuid);
		
		// Try spawn more
		MonsterManager monsterManager = MonsterManager.getManager();
		for (Location spawnSpot : spawnSpots) {
			spawnSpot.getWorld().spawnParticle(Particle.HEART, spawnSpot, 1, 0, 0, 0);
			if (!canSpawnAI(spawnSpot)) continue;
			
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
		}
		
		// Update ai marks spots
		for (MonsterPlayer monster : MonsterManager.getManager().getGamePlayers()) {
			if (monster.isAlive() && monster.getPlayer().isOnGround())
				addAISpawnLocation(monster.getLocation());
		}
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
				entity.kill();
		}
		
		spawnSpots.removeIf(location -> center.distance(location) <= range);
	}
	
	static {
		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		protocolManager.addPacketListener(new PacketAdapter(Game.getGame().getPlugin(), PacketType.Play.Server.NAMED_SOUND_EFFECT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				int sound = event.getPacket().getIntegers().read(0);
				if (sound == 431) {
					event.setCancelled(true);
				}
			}
		});
	}
	
	// ~~~~~~ TEAMS ~~~~~~
	private Team aiTeam;
	
	private void setupTeam() {
		String teamName = "AI";
		ChatColor teamColour = ChatColor.DARK_RED;
		
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		
		Team oldTeam = board.getTeam(teamName);
		if (oldTeam != null)
			oldTeam.unregister();
		
		aiTeam = board.registerNewTeam(teamName);
		
		aiTeam.setPrefix(String.valueOf(teamColour));
		aiTeam.setDisplayName(teamColour + teamName);
		
		aiTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
		aiTeam.setCanSeeFriendlyInvisibles(true);
		aiTeam.setAllowFriendlyFire(false);
	}
}
