package deimophobe.nightfall.map.region;

import deimophobe.nightfall.game.GameEntity;
import deimophobe.nightfall.game.GamePlayer;
import deimophobe.nightfall.map.GameMap;
import deimophobe.nightfall.map.InvalidMapConfigException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

/**
 * Created by Deimophobe on 21/01/17.
 */
public interface Region {
	boolean containsLocation(Location loc);
	default boolean containsPlayer(GamePlayer gamePlayer) {
		return containsLocation(gamePlayer.getLocation());
	}
	default boolean containsBlock(Block block) {
		return containsLocation(block.getLocation());
	}
	default boolean continsEntity(Entity entity) {
		return containsLocation(entity.getLocation());
	}
	default boolean continsGameEntity(GameEntity ge) {return containsLocation(ge.getLocation()); }
	
	static Region createRegion(GameMap map, ConfigurationSection section) throws InvalidMapConfigException {
		if (!section.contains("type")) {
			Bukkit.getLogger().severe("Regions must have a type!");
			return null;
		}
		
		String type = section.getString("type");
		switch (type) {
			case "spherical":
				return new SphericalRegion(map, section);
			case "cylindrical":
				return new CylindricalRegion(map, section);
			case "halfspace":
				return new HalfRegion(map, section);
			case "nullregion":
				return new NullRegion();
			case "or":
				return new OrRegion(map, section);
			case "and":
				return new AndRegion(map, section);
			default:
				Bukkit.getLogger().severe("Region type unknown: '"+type+"'");
				throw new InvalidMapConfigException("Unknown region type: '" + type + "' at " + section.getCurrentPath());
		}
	}
}
