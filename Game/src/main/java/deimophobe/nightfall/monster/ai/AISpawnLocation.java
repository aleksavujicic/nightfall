package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.util.WeightedSet;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 14/11/17.
 */
class AISpawnLocation {
	private final AIManager manager;
	private final Location location;
	private int life;
	private static final int LIFETIME = 60;
	
	AISpawnLocation(Location location) {
		this.manager = AIManager.getManager();
		this.location = location.getBlock().getLocation().add(0.5, 0, 0.5);
		this.life = LIFETIME;
	}
	
	void update() {
		for (Player debugger : Game.getGame().getOnlineDebugPlayers()) {
			showToPlayer(debugger);
		}
		
		if (!isValid()) {
			NightfallPlugin.logger().severe("Attempted to update dead spawn location!");
			return;
		}
		
		// Decay every update tick
		life--;
		
		trySpawnAIs();
	}
	
	private void trySpawnAIs() {
		// If failed to spawn, stop.
		if (Math.random() > manager.getBaseSpawnChance()) return;
		
		// Find closest dwarf and set as target.
		Dwarf closestDwarf = null;
		double closestDistance = 25;
		for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
			double distance = location.distance(dwarf.getLocation());
			if (distance <= 4) return; // Too close to dwarf, don't spawn
			
			if (distance <= closestDistance) {
				closestDwarf = dwarf;
				closestDistance = distance;
			}
		}
		
		// If no close enough dwarf, greatly reduce spawn chance
		if (closestDwarf == null) {
			if (Math.random() > 0.1) return;
		}
		
		// Spawn them
		AISpawner spawner = SPAWNERS.getRandom();
		int cost = spawner.spawn(location, closestDwarf);
		life -= cost;
	}
	
	boolean isWithinRange(Location loc, double range) {
		return (location.distance(loc) < range);
	}
	
	boolean isValid() {
		return life >= 0;
	}
	
	void kill() {
		life = 0;
	}
	
	void showToPlayer(Player player) {
		player.spawnParticle(Particle.HEART, location, 1, 0, 0, 0);
	}
	
	private static final WeightedSet<AISpawner> SPAWNERS = new WeightedSet<>(
			new SimpleAISpawner(95, (location, target) -> {
				int amtToSpawn = 1;
				double rand = Math.random();
				if (rand < 0.35) amtToSpawn++;
				if (rand < 0.15) amtToSpawn++;
				AIManager.getManager().spawnAIs(AIType.ZOMBIE, location, target, amtToSpawn);
				return 3 + amtToSpawn;
			})
//			new SimpleAISpawner(95, (location, target) -> {
//				AIManager.getManager().spawnAIs(AIType.FIRE_SKELLY, location, target, 1);
//				return 3;
//			})
	);
}
