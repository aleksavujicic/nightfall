package deimophobe.nightfall.common.loadout.save;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class LoadItem implements MenuItem<PlayerInfo> {
	private final int slot;
	private final ItemStack item;
	
	LoadItem(CustomItem template, int slot) {
		CustomItem saveItem = template.clone();
		saveItem.setName("Load Slot " + slot);
		saveItem.applyVariable("slotname", Integer.toString(slot));
		this.item = saveItem.createItemStack();
		
		this.slot = slot;
	}
	
	@Override
	public ItemStack getDisplayItem(MenuSession<PlayerInfo> session) {
		return item;
	}
	
	@Override
	public boolean onClick(MenuSession<PlayerInfo> session) {
		PlayerInfo info = session.getData();
		Loadout copy = info.getSavedLoadout(slot).createCopy();
		info.setLoadout(copy);
		
		return false;
	}
}
