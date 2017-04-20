package deimophobe.dvz.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/04/17.
 */
class SimpleBaseItem implements BaseItem {
	private final Material material;
	private final short damage;
	
	SimpleBaseItem(Material material) {
		this(material, (short) 0);
	}
	
	SimpleBaseItem(Material material, int damage) {
		this(material, (short) damage);
	}
	
	SimpleBaseItem(Material material, short damage) {
		this.material = material;
		this.damage = damage;
	}
	
	@Override
	public ItemStack createItem() {
		return new ItemStack(material, 1, damage);
	}
}
