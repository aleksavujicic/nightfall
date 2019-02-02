package deimophobe.nightfall.blocks.blocktype;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static deimophobe.nightfall.util.NFConditions.checkMaterialExtendsDataClass;
import static deimophobe.nightfall.util.NFConditions.checkMaterialIsBlock;

/**
 * Created by Deimophobe on 18/12/18.
 */
public class DataChecker<T extends BlockData> implements BlockMatcher {
	private final Material material;
	private final Class<T> dataClass;
	private final Predicate<T> dataChecker;
	
	public DataChecker(Material material, Class<T> dataClass, Predicate<T> dataChecker) {
		checkMaterialIsBlock(material);
		checkMaterialExtendsDataClass(material, dataClass);
		
		this.material = material;
		this.dataClass = dataClass;
		this.dataChecker = dataChecker;
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		Material type = block.getType();
		if (type != material) return false;
		
		MutableBoolean matches = new MutableBoolean(false);
		Util.safeCastBlockData(
				block,
				dataClass,
				data -> matches.setValue(dataChecker.test(data)),
				Misc.DO_NOTHING
		);
		return matches.getValue();
	}
}
