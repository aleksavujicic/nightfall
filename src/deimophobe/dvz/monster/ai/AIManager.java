package deimophobe.dvz.monster.ai;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.DwarfManager;
import deimophobe.dvz.monster.MonsterManager;
import deimophobe.dvz.monster.MonsterPlayer;
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
	
	private final static int MAX_AIS = 45;
	private final static double AI_SPAWN_CHANCE = 0.2;
	
	
	public void setup() {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateAIs();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 100, 100);
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
	
	// ------ SPAWN LOCATIONS ------
	private final Queue<Location> spawnSpots = new LinkedList<>();
	
	private final static int MAX_AI_MARKS = 90;
	private final static double SPAWN_THRESHOLD = 1;
	
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
	private boolean canSpawnAI(Location spawnSpot) {
		return  (aisSpawnable &&
				Game.getGame().getPhase().canAISpawn() &&
				ais.size() < MAX_AIS &&
				Math.random() < AI_SPAWN_CHANCE &&
				!ShrineManager.getManager().getShrine().getShrineProtection().containsLocation(spawnSpot));
	}
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		killAllAIs();
		return aisSpawnable;
	}
	
	
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Region shrineProt = ShrineManager.getManager().getShrine().getShrineProtection();
		Set<UUID> deadAIs = new HashSet<>();
		for (AIEntity ai : ais.values()) {
			if (!ai.hasTarget() || shrineProt.continsGameEntity(ai)) {
				ai.kill();
			}
			
			if (ai.isDead())
				deadAIs.add(ai.getUniqueId());
		}
		for (UUID uuid : deadAIs)
			ais.remove(uuid);
		
		// Try spawn more
		World world = Game.getGame().getWorld();
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
			Zombie ai = (Zombie) world.spawnEntity(spawnSpot, EntityType.ZOMBIE);
			ai.setCustomName(ChatColor.DARK_RED + Misc.getRandom(AI_NAMES));
			int speedLvl = (ai.isBaby() ? 0 : 3);
			ai.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30000, speedLvl, false,false), true);
			ai.getEquipment().clear();
			ai.getEquipment().setItemInMainHand(new ItemStack(Material.SHEARS, 1, (short) 100));
			ai.setTarget(closestDwarf.getPlayer());
			monsterManager.addToTeam(ai.getUniqueId().toString());
			
			ais.put(ai.getUniqueId(), new AIEntity(ai));
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
	
	public void killAllAIs() {
		for (AIEntity ai : ais.values()) {
			ai.kill();
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
}
