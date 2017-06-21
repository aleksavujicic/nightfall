package deimophobe.dvz.shrine.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 21/01/17.
 */
class HalfRegion implements Region {
	
	enum Coordinate {
		X_PLUS, X_MINUS,
		Y_PLUS, Y_MINUS,
		Z_PLUS, Z_MINUS,
	}
	
	private final Coordinate coordinate;
	private final double divider;
	
	HalfRegion(Coordinate coordinate, double divider) {
		this.coordinate = coordinate;
		this.divider = divider;
	}
	
	HalfRegion(ConfigurationSection section) {
		String coord = section.getString("coordinate");
		switch (coord) {
			case "x+":
				coordinate = Coordinate.X_PLUS;
				break;
			case "x-":
				coordinate = Coordinate.X_MINUS;
				break;
			case "y+":
				coordinate = Coordinate.Y_PLUS;
				break;
			case "y-":
				coordinate = Coordinate.Y_MINUS;
				break;
			case "z+":
				coordinate = Coordinate.Z_PLUS;
				break;
			case "z-":
				coordinate = Coordinate.Z_MINUS;
				break;
			default:
				coordinate = null;
				Bukkit.getLogger().severe("Half region coordinate unknown: '"+coord+"'");
				break;
		}
		this.divider = section.getDouble("divider");
	}
	
	
	@Override
	public boolean containsLocation(Location loc) {
		switch (coordinate) {
			case X_PLUS:
				return loc.getX() >= divider;
			case X_MINUS:
				return loc.getX() <= divider;
			case Y_PLUS:
				return loc.getY() >= divider;
			case Y_MINUS:
				return loc.getY() <= divider;
			case Z_PLUS:
				return loc.getZ() >= divider;
			case Z_MINUS:
				return loc.getZ() <= divider;
		}
		Bukkit.getLogger().warning("HalfRegion Coordinate not set properly?!");
		return false;
	}
}
