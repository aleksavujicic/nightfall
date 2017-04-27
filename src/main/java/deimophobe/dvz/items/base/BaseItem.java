package deimophobe.dvz.items.base;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
public interface BaseItem extends Cloneable {
	ItemStack createItem();
	boolean isSimilar(BaseItem item);
	boolean isSimilar(ItemStack item);
	
	BaseItem clone();
}
