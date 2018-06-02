package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 2/06/18.
 */
public class MenuOpenerItem<T extends SessionData> extends SimpleItem<T> {
	private final MainMenu<?> openMenu;
	
	public MenuOpenerItem(ItemStack item, MainMenu<?> openMenu) {
		super(item);
		this.openMenu = openMenu;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		session.openNewSession(openMenu);
		return false;
	}
}
