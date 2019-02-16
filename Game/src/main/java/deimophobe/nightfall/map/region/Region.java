package deimophobe.nightfall.map.region;

import deimophobe.nightfall.game.entity.GameEntity;
import deimophobe.nightfall.map.GameMap;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

/**
 * Created by Deimophobe on 21/01/17.
 */
@FunctionalInterface
public interface Region {
	boolean containsPosition(double x, double y, double z);
	
	
	default boolean containsLocation(Location location) {
		return containsPosition(location.getX(), location.getY(), location.getZ());
	}
	
	default boolean containsBlock(Block block) {
		return containsLocation(block.getLocation().add(0.5, 0.5, 0.5));
	}
	
	default boolean containsEntity(Entity entity) {
		return containsLocation(entity.getLocation());
	}
	
	default boolean containsEntity(GameEntity gameEntity) {return containsLocation(gameEntity.getLocation()); }
	
	
	
	static Region not(Region region) {
		return (x,y,z) -> !region.containsPosition(x, y, z);
	}
	
	static Region and(Region... regions) {
		return (x,y,z) -> {
			for (Region region : regions) {
				if (!region.containsPosition(x, y, z)) {
					return false;
				}
			}
			return true;
		};
	}
	
	static Region or(Region... regions) {
		return (x,y,z) -> {
			for (Region region : regions) {
				if (region.containsPosition(x, y, z)) {
					return true;
				}
			}
			return false;
		};
	}
	
	Region NULL_REGION = new NullRegion();
	@Deprecated
	static Region createRegion(GameMap map, ConfigurationSection region) {
		return NULL_REGION;
	}
}
