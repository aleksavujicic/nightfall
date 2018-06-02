package deimophobe.nightfall.common.loadout.save;

import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import deimophobe.nightfall.common.player.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
class ExportItem extends SimpleItem<PlayerInfo> {
	ExportItem(ItemStack item) {
		super(item);
	}
	
	@Override
	public boolean onClick(MenuSession<PlayerInfo> session) {
		Player player = session.getPlayer();
		Loadout loadout = session.getData().getLoadout();
		
		String base64 = loadout.getBase64();
		
		TextComponent message = new TextComponent("Click to copy you're loadout ");
		message.setColor(ChatColor.YELLOW);
		
		TextComponent copyComponent = new TextComponent("here");
		copyComponent.setUnderlined(true);
		copyComponent.setColor(ChatColor.AQUA);
		copyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, base64));
		
		player.spigot().sendMessage(message, copyComponent);
		
		return false;
	}
}
