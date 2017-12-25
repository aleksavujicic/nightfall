package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 24/12/17.
 */
public abstract class SelectableItem<T extends SessionData> extends SimpleItem<T> {
	
	public SelectableItem(ItemStack item) {
		super(item);
	}
	
	protected abstract boolean isSelected(MenuSession<T> session);
	
	@Override
	public ItemStack getDisplayItem(MenuSession<T> session) {
		ItemStack item = super.getDisplayItem(session);
		
		if (isSelected(session)) {
			item = item.clone();
			item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			return item;
		} else {
			return item;
		}
	}
}
