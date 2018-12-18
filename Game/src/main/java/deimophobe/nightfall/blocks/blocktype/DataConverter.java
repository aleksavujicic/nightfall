package deimophobe.nightfall.blocks.blocktype;

import deimophobe.nightfall.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static deimophobe.nightfall.util.NFConditions.checkMaterialExtendsDataClass;

/**
 * Created by Deimophobe on 18/12/18.
 */
public class DataConverter<T extends BlockData> implements BlockPlacer {
	private final Class<T> dataClass;
	private final Function<T, T> dataConverter;
	private final BlockPlacer defaultPlacer;
	
	public DataConverter(@NotNull Class<T> dataClass, @NotNull Function<T, T> dataConverter) {
		this(dataClass, dataConverter, BlockPlacer.FAILED);
	}
	
	public DataConverter(@NotNull Class<T> dataClass, @NotNull Function<T, T> dataConverter, @NotNull BlockPlacer defaultPlacer) {
		this.dataClass = dataClass;
		this.dataConverter = dataConverter;
		this.defaultPlacer = defaultPlacer;
	}
	
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		Util.safeCastBlockData(
				block,
				dataClass,
				oldData -> {
					BlockData newData = dataConverter.apply(oldData);
					block.setBlockData(newData);
				},
				() -> defaultPlacer.setAtBlock(block)
		);
	}
	
	private static <S extends BlockData> Function<S, S> materialConverterFunction(@NotNull Material material, @NotNull BiConsumer<S, S> defaultApplier) {
		return oldData -> (S) material.createBlockData(newData -> defaultApplier.accept(oldData, (S) newData));
	}
	
	public static DataConverter<Stairs> stairConverter(Material stairMaterial) {
		checkMaterialExtendsDataClass(stairMaterial, Stairs.class);
		
		return new DataConverter<>(
				Stairs.class,
				materialConverterFunction(stairMaterial, (oldStairs, newStairs) -> {
					BlockFace direction = oldStairs.getFacing();
					Bisected.Half half = oldStairs.getHalf();
					Stairs.Shape shape = oldStairs.getShape();
					boolean waterlogged = oldStairs.isWaterlogged();
					
					newStairs.setFacing(direction);
					newStairs.setHalf(half);
					newStairs.setShape(shape);
					newStairs.setWaterlogged(waterlogged);
				})
		);
	}
	public static DataConverter<Slab> slabConverter(Material slabMaterial) {
		checkMaterialExtendsDataClass(slabMaterial, Slab.class);
		
		return new DataConverter<>(
				Slab.class,
				materialConverterFunction(slabMaterial, (oldSlab, newSlab) -> {
					Slab.Type type = oldSlab.getType();
					boolean waterlogged = oldSlab.isWaterlogged();
					
					newSlab.setType(type);
					newSlab.setWaterlogged(waterlogged);
				})
		);
	}
	
	public static BlockInteracter stairInteracter(Material stairMaterial) {
		return new MaterialBlock(stairMaterial).withPlacer(stairConverter(stairMaterial));
	}
	
	public static BlockInteracter slabInteracter(Material slabMaterial) {
		return new MaterialBlock(slabMaterial).withPlacer(slabConverter(slabMaterial));
	}
}
