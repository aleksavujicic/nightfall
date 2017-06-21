package deimophobe.dvz.shrine.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 31/03/17.
 */
public interface CenteredRegion extends Region {
	Location getCenter();
	
	static CenteredRegion createRegion(ConfigurationSection section) {
		if (!section.contains("type")) {
			Bukkit.getLogger().severe("Regions must have a type!");
			return null;
		}
		
		String type = section.getString("type");
		switch (type) {
			case "spherical":
				return new SphericalRegion(section);
			default:
				Bukkit.getLogger().severe("Centered region type unknown: '"+type+"'");
				return null;
		}
	}
}
