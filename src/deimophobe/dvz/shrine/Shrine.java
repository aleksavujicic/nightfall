package deimophobe.dvz.shrine;

import deimophobe.dvz.Game;
import deimophobe.dvz.shrine.Region.Region;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 21/01/17.
 */
public class Shrine {
	private final String name;
	private final Location mobSpawn;
	
	private final Region mobProtection;
	private final Region shrineProtection;
	private final Region shrineRegion;
	
	private int shrinePower;
	private final int maxShrinePower;
	
	private final double goldWeight;
	
	public String getName() {
		return name;
	}
	public Location getMobSpawn() {
		return mobSpawn;
	}
	
	public Region getMobProtection() {
		return mobProtection;
	}
	public Region getShrineProtection() {
		return shrineProtection;
	}
	public Region getShrineRegion() {
		return shrineRegion;
	}
	
	public double getGoldWeight() { return goldWeight; }
	
	public Shrine(String name, Location mobSpawn, Region mobProtection, Region shrineProtection, Region shrineRegion, int maxShrinePower, double goldWeight) {
		this.name = name;
		this.mobSpawn = mobSpawn;
		this.mobProtection = mobProtection;
		this.shrineProtection = shrineProtection;
		this.shrineRegion = shrineRegion;
		
		this.shrinePower = maxShrinePower;
		this.maxShrinePower = maxShrinePower;
		this.goldWeight = goldWeight;
	}
	
	public static Shrine createShrine(ConfigurationSection section) {
		
		String name = section.getString("name");
		Location mobSpawn = Game.createLocation(section.getDoubleList("mobspawn"));
		
		Region mobProt = Region.createRegion(section.getConfigurationSection("mobprot"));
		Region shrineProt = Region.createRegion(section.getConfigurationSection("shrineprot"));
		Region shrine = Region.createRegion(section.getConfigurationSection("shrine"));
		
		int maxShrinePower = section.getInt("power");
		double goldWeight = section.getDouble("goldweight");
		
		return new Shrine(name, mobSpawn, mobProt, shrineProt, shrine, maxShrinePower, goldWeight);
	}
	
	public boolean damageShrine(int damage) {
		shrinePower -= damage;
		return (shrinePower <= 0);
	}
	
	public float getFractionalShrinePower() {
		return (float) shrinePower/maxShrinePower;
	}
}
