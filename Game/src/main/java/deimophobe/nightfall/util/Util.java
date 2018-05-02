package deimophobe.nightfall.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class Util {
	
	public static boolean isWater(Block block) {
		switch (block.getType()) {
			case WATER:
			case STATIONARY_WATER:
				return true;
				
			default:
				return false;
		}
	}
	
	public static Location getSphericalPosition(Location center, double radius, double polar, double azimuthal) {
		double x = radius * Math.sin(polar) * Math.cos(azimuthal);
		double z = radius * Math.sin(polar) * Math.sin(azimuthal);
		double y = radius * Math.cos(polar);
		
		return center.clone().add(x,y,z);
	}
	
	public static void doNTimes(int n, Runnable runnable) {
		for (int i=0; i<n; i++) {
			runnable.run();
		}
	}
}
