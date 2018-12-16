package deimophobe.nightfall.blocks.blocktype;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.ItemMatcher;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/12/18.
 */
public class RepeatMaterial implements BlockInteracter, ItemMatcher {
	private static final Material[] MATERIAL_VALUES = Material.values();
	
	private final Set<Material> materials = new HashSet<>();
	private final Material defaultMaterial;
	
	private RepeatMaterial(final String prefix, final String suffix, final String defaultValue, final String... values) throws UnknownEnumElementException {
		String defaultName = prefix + defaultValue + suffix;
		defaultMaterial = Misc.getEnumMemberFromString(defaultName, MATERIAL_VALUES, "material");
		materials.add(defaultMaterial);
		
		for (String value : values) {
			String name = prefix + value + suffix;
			Material material = Misc.getEnumMemberFromString(name, MATERIAL_VALUES, "material");
			materials.add(material);
		}
	}
	
	@Override
	public boolean matchesBlock(@NotNull Block block) {
		Material type = block.getType();
		return materials.contains(type);
	}
	
	@Override
	public void setAtBlock(@NotNull Block block) {
		block.setType(defaultMaterial);
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack item) {
		Material type = item.getType();
		return materials.contains(type);
	}
	
	Set<Material> getMaterials() {
		return materials;
	}
	
	
	
	public static RepeatMaterial colourMaterial(String suffix) {
		try {
			return new RepeatMaterial("", suffix,
					"white_",
					"orange_",
					"magenta_",
					"light_blue_",
					"yellow_",
					"lime_",
					"pink_",
					"gray_",
					"light_gray_",
					"cyan_",
					"purple_",
					"blue_",
					"brown_",
					"green_",
					"red_",
					"black_"
			);
		} catch (UnknownEnumElementException e) {
			throw new RuntimeException("Bad colour suffix ('" + suffix + "')", e);
		}
	}
	
	public static RepeatMaterial woodMaterial(String suffix) {
		return woodMaterial("", suffix);
	}
	
	public static RepeatMaterial woodMaterial(String prefix, String suffix) {
		try {
			return new RepeatMaterial(prefix, suffix,
					"oak_",
					"acacia_",
					"birch_",
					"dark_oak_",
					"jungle_",
					"spruce_"
			);
		} catch (UnknownEnumElementException e) {
			throw new RuntimeException("Bad wood prefix ('" + prefix + "') or suffix ('" + suffix + "')", e);
		}
	}
	
	public static RepeatMaterial coralMaterial(String prefix, String suffix) {
		try {
			return new RepeatMaterial(prefix, suffix,
					"tube_",
					"brain_",
					"bubble_",
					"fire_",
					"horn_"
			);
		} catch (UnknownEnumElementException e) {
			throw new RuntimeException("Bad coral prefix ('" + prefix + "') or suffix ('" + suffix + "')", e);
		}
	}
}
