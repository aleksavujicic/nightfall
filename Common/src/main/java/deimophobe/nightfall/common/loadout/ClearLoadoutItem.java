package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class ClearLoadoutItem implements MenuItem<Loadout> {
	private final ItemStack item;
	
	ClearLoadoutItem(ItemStack item) {
		this.item = item;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<Loadout> session) {
		return item;
	}
	
	@Override
	public boolean onClick(MenuSession<Loadout> session) {
		session.getData().clear();
		return true;
	}
	
}
