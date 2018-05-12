package deimophobe.nightfall.common.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Deimophobe on 7/05/18.
 */
public class MaterialMatcher implements ItemMatcher {
	private final Material material;
	
	public MaterialMatcher(Material material) {
		this.material = material;
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack item) {
		if (item == null) return false;
		return item.getType() == material;
	}
}
