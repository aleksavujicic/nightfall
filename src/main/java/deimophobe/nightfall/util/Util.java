package deimophobe.nightfall.util;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.MonsterEntity;
import deimophobe.nightfall.monster.MonsterManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class Util {
	
	public static boolean fireHitscan(
			Location location,
			Vector direction,
			double range,
			double thickness,
			double particlePeriod,
			Consumer<Location> particlePlacer,
			Consumer<Dwarf> dwarfConsumer,
			Consumer<MonsterEntity> mobConsumer
	) {
		direction = direction.clone().normalize();
		
		Vector delta = direction.clone().multiply(particlePeriod);
		int times = (int) (range/particlePeriod);
		Location particlePos = location.clone();
		
		// Place particles if placer not null
		boolean success = true;
		if (particlePlacer != null) {
			for (int i = 0; i <= times; i++) {
				particlePos.add(delta);
				particlePlacer.accept(particlePos);
				
				// Stop beam if it hits a block
				if (particlePos.getBlock().getType().isSolid()) {
					range = location.distance(particlePos);
					success = false;
					break;
				}
			}
		}
		if (dwarfConsumer != null) {
			consumeEntitiesInLine(location, direction, range, thickness, dwarfConsumer, DwarfManager.getManager().getDwarves());
		}
		
		if (mobConsumer != null) {
			consumeEntitiesInLine(location, direction, range, thickness, mobConsumer, MonsterManager.getManager().getAliveMobsAndAIs());
		}
		
		return success;
	}
	
	private static  <P extends GameEntity> void consumeEntitiesInLine(
			Location location,
			Vector direction,
			double range,
			double thickness,
			Consumer<P> applier,
			Collection<P> entities
	) {
		
		double maxDistance = Math.max(range, thickness);
		for (P entity : entities) {
			
			// Skip if further than distance shot or too close
			Location entityLoc = entity.getEyeLocation();
			double distance = location.distance(entityLoc);
			if (distance <= maxDistance) {
				// Find if close enough to beam
				Vector monsterOffset = entityLoc.clone().subtract(location).toVector();
				Vector radialPostion = direction.clone().multiply(monsterOffset.clone().dot(direction)); // ((m - p) dot u) times u
				double radialOffset = radialPostion.subtract(monsterOffset).length();
				
				// If close enough to give dwarf proc
				if (radialOffset <= thickness) {
					applier.accept(entity);
				}
			}
		}
	}
	
	public static boolean isWater(Block block) {
		switch (block.getType()) {
			case WATER:
			case STATIONARY_WATER:
				return true;
				
			default:
				return false;
		}
	}
}
