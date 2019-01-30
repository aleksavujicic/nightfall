package deimophobe.nightfall.common.menu.item;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.SessionData;
import deimophobe.nightfall.common.menu.submenu.DynamicMenu;
import deimophobe.nightfall.common.menu.submenu.SubMenu;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 15/01/19.
 */
public class DynamicPageChanger<T extends SessionData> extends SimpleItem<T> {
	
	private final DynamicMenu<T> menu;
	private final SubMenu<T> subMenu;
	
	public DynamicPageChanger(ItemStack item, DynamicMenu<T> menu, SubMenu<T> subMenu) {
		super(item);
		this.menu = menu;
		this.subMenu = subMenu;
	}
	
	@Override
	public boolean onClick(MenuSession<T> session) {
		menu.setMenu(session, subMenu);
		return true;
	}
}
