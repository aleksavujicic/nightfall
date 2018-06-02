package deimophobe.nightfall.common.loadout;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class PointsMenuItem implements MenuItem<Loadout> {
	private final ItemStack pointsItem;
	private final ItemStack emptyItem;
	
	PointsMenuItem(ItemStack pointsItem, ItemStack emptyItem) {
		this.pointsItem = pointsItem;
		this.emptyItem = emptyItem;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<Loadout> session) {
		int amt = session.getData().getRemainingPoints();
		
		if (amt == 0) {
			return emptyItem;
		} else {
			ItemStack item = pointsItem.clone();
			item.setAmount(amt);
			return item;
		}
	}
	
	@Override
	public boolean onClick(MenuSession<Loadout> session) {
		session.getData().clear();
		return true;
	}
}
