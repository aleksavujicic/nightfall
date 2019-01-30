package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.menu.submenu.DynamicMenu;
import deimophobe.nightfall.common.menu.submenu.SubMenu;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/01/19.
 */
public class DynamicPageResetter<T extends SessionData> extends SimpleItem<T> {
	
	private final DynamicMenu<T> menu;
	
	public DynamicPageResetter(ItemStack item, DynamicMenu<T> menu) {
		super(item);
		this.menu = menu;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		menu.resetMenu(session);
		return true;
	}
}
