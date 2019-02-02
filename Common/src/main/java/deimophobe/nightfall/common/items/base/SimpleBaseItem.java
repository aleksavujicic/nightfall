package deimophobe.nightfall.common.items.base;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class SimpleBaseItem implements BaseItem {
	private final Material material;
	private final short damage;
	
	public SimpleBaseItem(Material material) {
		this(material, (short) 0);
	}
	
	public SimpleBaseItem(Material material, int damage) {
		this(material, (short) damage);
	}
	
	public SimpleBaseItem(Material material, short damage) {
		if (material == null) throw new NullPointerException("Material for base item cannot be null.");
		
		this.material = material;
		this.damage = damage;
	}
	
	@Override
	public ItemStack createItem() {
		return new ItemStack(material, 1, damage);
	}
	
	
	@Override
	public boolean isSimilar(BaseItem item) {
		if (item instanceof SimpleBaseItem) {
			return (
					material == ((SimpleBaseItem) item).material &&
					damage == ((SimpleBaseItem) item).damage
			);
		}
		return false;
	}
	
	@Override
	public boolean doesItemMatch(ItemStack item) {
		return (
			material == item.getType() &&
			damage == item.getDurability()
		);
	}
	
	@Override
	public SimpleBaseItem clone() {
		return new SimpleBaseItem(material, damage);
	}
}
