package deimophobe.nightfall.game;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Deimophobe on 11/12/18.
 */
public class MenuLobbyItem implements LobbyItem {
	private final CustomItem item;
	private final MainMenu<?> menu;
	
	public MenuLobbyItem(CustomItem item, MainMenu<?> menu) {
		this.item = item;
		this.menu = menu;
	}
	
	public MenuLobbyItem(CustomItem item, Class<? extends MainMenu<?>> menuClass) {
		this.item = item;
		this.menu = MenuManager.getManager().getMenu(menuClass);
	}
	
	@Override
	public CustomItem getItem(@Nullable Player player) {
		return item;
	}
	
	@Override
	public void onClick(Player player) {
		if (MenuManager.getManager().hasOpenSession(player)) return;
		menu.startSession(player);
	}
}
