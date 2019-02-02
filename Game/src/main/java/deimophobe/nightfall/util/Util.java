package deimophobe.nightfall.util;

import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MetaIndex;
import me.libraryaddict.disguise.disguisetypes.watchers.PlayerWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 29/12/17.
 */
public class Util {
	private Util() {}
	
	public static boolean isWater(Block block) {
		switch (block.getType()) {
			case WATER:
				return true;
				
			default:
				return false;
		}
	}
	
	public static boolean isRailStraight(Rail.Shape shape) {
		switch (shape) {
			case NORTH_SOUTH:
			case EAST_WEST:
			case ASCENDING_EAST:
			case ASCENDING_WEST:
			case ASCENDING_NORTH:
			case ASCENDING_SOUTH:
				return true;
			case SOUTH_EAST:
			case SOUTH_WEST:
			case NORTH_WEST:
			case NORTH_EAST:
				return false;
		}
		return false;
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
	
	public static boolean materialsExtendsBlockData(@NotNull Material material, @NotNull Class<? extends BlockData> dataClass) {
		checkNotNull(material, "Material must not be null.");
		checkNotNull(dataClass, "Data class must not be null.");
		checkArgument(material.isBlock(), "Material '%s' must be a block.", material);
		return dataClass.isInstance(material.createBlockData());
	}
	
	@Deprecated
	public static void setSkinByteOnPlayerDisguise(PlayerWatcher watcher, byte flag) {
		try {
			Method method = FlagWatcher.class.getDeclaredMethod("setData", MetaIndex.class, Object.class);
			method.setAccessible(true);
			method.invoke(watcher, MetaIndex.PLAYER_SKIN, flag);
			
			Method method2 = FlagWatcher.class.getDeclaredMethod("sendData", MetaIndex[].class);
			method2.setAccessible(true);
			method2.invoke(watcher, (Object) new MetaIndex[]{MetaIndex.PLAYER_SKIN});
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public static Item spawnDecorativeItem(@NotNull Location location, @NotNull ItemStack itemStack, int duration) {
		Item item = location.getWorld().dropItem(location, itemStack);
		item.setPortalCooldown(1000000);
		item.setPickupDelay(32767); // Never
		item.setTicksLived(6000 - duration);
		return item;
	}
}
