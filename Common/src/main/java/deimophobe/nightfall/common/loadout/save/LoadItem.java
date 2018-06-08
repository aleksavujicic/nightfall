package deimophobe.nightfall.common.loadout.save;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class LoadItem implements MenuItem<PlayerInfo> {
	private final String slotName;
	private final int slot;
	private final ItemStack item;
	
	LoadItem(CustomItem template, int slot) {
		this.slotName = Integer.toString(slot + 1);
		this.slot = slot;
		
		CustomItem loadItem = template.clone();
		loadItem.setName("Load Slot " + slotName);
		loadItem.applyVariable("slotname", slotName);
		this.item = loadItem.createItemStack();
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
		
		Player player = session.getPlayer();
		player.playSound(player.getLocation(),"ui.button.click", 1f, 0.8f);
		player.sendMessage(ChatColor.YELLOW + "Loaded loadout from slot " + slotName);
		
		return false;
	}
}
