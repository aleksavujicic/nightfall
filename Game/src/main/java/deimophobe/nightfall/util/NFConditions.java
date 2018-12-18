package deimophobe.nightfall.util;

import deimophobe.nightfall.NightfallPlugin;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

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
	
	public static void checkMaterialExtendsDataClass(@NotNull Material material, @NotNull Class<? extends BlockData> dataClass) {
		checkArgument(
				Util.materialsExtendsBlockData(material, dataClass),
				"Material '%s' creates a block that is not an instance of %s.",
				material, dataClass.getCanonicalName()
		);
	}
	
}
