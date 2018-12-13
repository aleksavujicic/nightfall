package deimophobe.nightfall.util;

import deimophobe.nightfall.NightfallPlugin;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 13/12/18.
 */
public class NFConditions {
	public static void checkVelocityParameter(double value, String name) {
		checkArgument(-4 <= value && value <= 4, "Velocity coordinate '%s' must be at most 4 (got %s)", name, value);
	}
	public static void warnVelocityParameter(double value, String name) {
		try {
			checkVelocityParameter(value, name);
		} catch (IllegalArgumentException e) {
			NightfallPlugin.logger().warning(e.getMessage());
			e.printStackTrace();
		}
	}
}
