package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;

import java.util.function.BiFunction;

/**
 * Created by Deimophobe on 13/05/18.
 */
class SimpleAISpawner implements AISpawner {
	private final double weight;
	private final BiFunction<Location, Dwarf, Integer> spawner;
	
	SimpleAISpawner(double weight, BiFunction<Location, Dwarf, Integer> spawner) {
		this.weight = weight;
		this.spawner = spawner;
	}
	
	@Override
	public double getWeight() {
		return weight;
	}
	
	@Override
	public int spawn(Location location, Dwarf target) {
		return spawner.apply(location, target);
	}
}
