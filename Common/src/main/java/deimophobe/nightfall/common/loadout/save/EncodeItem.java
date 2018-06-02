package deimophobe.nightfall.common.loadout.save;

import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import deimophobe.nightfall.common.player.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class EncodeItem extends SimpleItem<PlayerInfo> {
	EncodeItem(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean onClick(MenuSession<PlayerInfo> session) {
		Player player = session.getPlayer();
		Loadout loadout = session.getData().getLoadout();
		
		String base64 = loadout.getBase64();
		player.sendMessage(base64);
		
		return false;
	}
}
