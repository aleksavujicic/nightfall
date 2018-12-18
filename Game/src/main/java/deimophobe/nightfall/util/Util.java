package deimophobe.nightfall.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class Util {
	
	public static boolean isWater(Block block) {
		switch (block.getType()) {
			case WATER:
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
	
	public static <T extends BlockData> void safeCastBlockData(@NotNull Block block, @NotNull Class<T> dataClass, @NotNull Consumer<T> blockDataConsumer) {
		safeCastBlockData(block, dataClass, blockDataConsumer, () -> {
			new ClassCastException("Failed to cast block '" + block.getType() + "', to data class '" + dataClass.getCanonicalName() + "'.").printStackTrace();
		});
	}
	
	public static <T extends BlockData> void safeCastBlockData(@NotNull Block block, @NotNull Class<T> dataClass, @NotNull Consumer<T> blockDataConsumer, @NotNull Runnable onFail) {
		checkNotNull(block, "Block must not be null.");
		checkNotNull(dataClass, "Data class must not be null.");
		checkNotNull(blockDataConsumer, "Block data consumer must not be null.");
		checkNotNull(onFail, "Fail runnable must not be null.");
		
		BlockData data = block.getBlockData();
		if (dataClass.isInstance(data)) {
			//noinspection unchecked
			blockDataConsumer.accept((T) data);
		} else {
			onFail.run();
		}
	}
	
	public static <T extends BlockData> boolean materialsExtendsBlockData(@NotNull Material material, @NotNull Class<T> dataClass) {
		checkNotNull(material, "Material must not be null.");
		checkNotNull(dataClass, "Data class must not be null.");
		checkArgument(material.isBlock(), "Material '%s' must be a block.", material);
		return dataClass.isInstance(material.createBlockData());
	}
}
