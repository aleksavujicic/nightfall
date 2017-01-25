package deimophobe.dvz.shrine;

import deimophobe.dvz.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;

/**
 * Created by Deimophobe on 21/01/17.
 */
public interface Region {
	boolean containsLocation(Location loc);
	default boolean containsPlayer(GamePlayer gamePlayer) {
		return containsLocation(gamePlayer.getPlayer().getLocation());
	}
	default boolean containsBlock(Block block) {
		return containsLocation(block.getLocation());
	}
	default boolean continsEntity(Entity entity) {
		return containsLocation(entity.getLocation());
	}
	
	static Region createRegion(ConfigurationSection section) {
		if (!section.contains("type")) {
			Bukkit.getLogger().severe("Regions must have a type!");
			return null;
		}
		
		String type = section.getString("type");
		switch (type) {
			case "spherical":
				return new SphericalRegion(section);
			case "halfspace":
				return new HalfRegion(section);
			case "nullregion":
				return new NullRegion();
			default:
				Bukkit.getLogger().severe("Region type unknown: '"+type+"'");
				return null;
		}
	}
	
}
