package deimophobe.nightfall.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.function.Consumer;

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
	
	public static <T extends BlockData> void safeCastBlockData(Block block, Class<T> dataClass, Consumer<T> blockDataConsumer) {
		safeCastBlockData(block, dataClass, blockDataConsumer, () -> {
			new ClassCastException("Failed to cast block '" + block.getType() + "', to data class '" + dataClass.getCanonicalName() + "'.").printStackTrace();
		});
	}
	
	public static <T extends BlockData> void safeCastBlockData(Block block, Class<T> dataClass, Consumer<T> blockDataConsumer, Runnable onFail) {
		BlockData data = block.getBlockData();
		if (dataClass.isInstance(data)) {
			//noinspection unchecked
			blockDataConsumer.accept((T) data);
		} else {
			onFail.run();
		}
	}
}
