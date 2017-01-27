package deimophobe.dvz.monster.ai;

import deimophobe.dvz.Game;
import deimophobe.dvz.monster.MobManager;
import deimophobe.dvz.monster.PlayerMonster;
import deimophobe.dvz.shrine.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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
	private final static int MAX_AI_MARKS = 60;
	private final static double AI_SPAWN_CHANCE = 0.2;
	
	//private final static Set<String> AI_NAMES;
	
	private final Queue<Location> spawnSpots = new LinkedList<>();
	private final Map<UUID, AIEntity> ais = new HashMap<>();
	
	private boolean aisSpawnable = true;
	
	public void setup() {
		new BukkitRunnable() {
			@Override
			public void run() {
				updateAIs();
			}
		}.runTaskTimer(Game.getGame().getPlugin(), 100, 140);
	}
	
	private void updateAIs() {
		// Get rid of unnecessary ai
		Region shrineProt = Game.getGame().getShrine().getShrineProtection();
		Set<UUID> deadAIs = new HashSet<>();
		for (AIEntity ai : ais.values()) {
			Creature entity = ai.getEntity();
			if (entity.getTarget() == null || shrineProt.continsEntity(entity)) {
				entity.remove();
				//entity.damage(1000);
			}
			
			if (entity.isDead())
				deadAIs.add(entity.getUniqueId());
		}
		for (UUID uuid : deadAIs)
			ais.remove(uuid);
		
		// Try spawn more
		trySpawnAI();
		
		// Update ai marks spots
		for (PlayerMonster monster : MobManager.getManager().getMobs()) {
			if (monster.isAlive() && monster.getPlayer().isOnGround())
				addAISpawnLocation(monster.getLocation());
		}
	}
	
	private void trySpawnAI() {
		World world = Game.getGame().getWorld();
		MobManager mobManager = MobManager.getManager();
		for (Location spawnSpot : spawnSpots) {
			if (!canSpawnAI()) continue;
			
			// Create zombie with all right stuff
			Zombie ai = (Zombie) world.spawnEntity(spawnSpot, EntityType.ZOMBIE);
			ai.setCustomName(ChatColor.DARK_RED + "Rawb the AI");
			int speedLvl = (ai.isBaby() ? 0 : 3);
			ai.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30000, speedLvl, false,false), true);
			ai.getEquipment().clear();
			ai.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE, 1, (short) 100));
			mobManager.addToTeam(ai.getUniqueId().toString());
			
			ais.put(ai.getUniqueId(), new AIEntity(ai));
		}
	}
	
	private boolean canSpawnAI() {
		return  (aisSpawnable &&
				Game.getGame().getPhase().canAISpawn() &&
				ais.size() < MAX_AIS &&
				Math.random() < AI_SPAWN_CHANCE);
	}
	
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
	
	public boolean toggleAISpawn() {
		aisSpawnable = !aisSpawnable;
		return aisSpawnable;
	}
	
	public AIEntity getAI(Entity entity) {
		return ais.get(entity.getUniqueId());
	}
	
	public void killAllAIs() {
		for (AIEntity ai : ais.values()) {
			ai.getEntity().damage(1000);
		}
		ais.clear();
	}
	
	public Collection<AIEntity> getAIs() {
		return ais.values();
	}
}
