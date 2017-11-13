package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.Bukkit;
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
	
	AISpawnLocation(Location location) {
		this.manager = AIManager.getManager();
		this.location = location;
		this.life = 50;
	}
	
	void update() {
		for (Player debugger : Game.getGame().getOnlineDebugPlayers()) {
			debugger.spawnParticle(Particle.HEART, location, 1, 0, 0, 0);
		}
		
		if (!isValid()) {
			Bukkit.getLogger().severe("Attempted to update dead spawn location!");
			return;
		}
		
		// Decay every update tick
		life--;
		
		// Every now and then try decay based on proximity to dwarves
		if (Math.random() < 0.2) {
			for (Dwarf dwarf : DwarfManager.getManager().getGamePlayers()) {
				if (location.distance(dwarf.getLocation()) <= 5) {
					life--;
				}
			}
		}
		
		trySpawnAIs();
	}
	
	private void trySpawnAIs() {
		double spawnChance = manager.getBaseSpawnChance() * (1 + (double) life/50);
		
		// Find closest dwarf and set as target. If no such dwarf, much smaller spawn chance;
		Dwarf closestDwarf = DwarfManager.getManager().getNearest(location);
		if (closestDwarf == null || closestDwarf.distanceTo(location) > 25) {
			closestDwarf = null;
			spawnChance *= 0.1;
		}
		
		// If failed to spawn, stop.
		if (Math.random() > spawnChance) return;
		
		int amtToSpawn = 1;
		double rand = Math.random();
		if (rand < 0.5) amtToSpawn++;
		if (rand < 0.25) amtToSpawn++;
		
		life -= amtToSpawn*5;
		manager.spawnAIs(location, closestDwarf, amtToSpawn);
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
}
