package deimophobe.dvz.menu;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 2/03/17.
 */
public class GlobalMenuList {
	private static final Map<String, Menu> menus = new HashMap<>();
	public static void registerMenu(Menu menu) {
		menus.put(menu.getTitle(), menu);
	}
	
	public static Menu getMenu(Inventory inv) {
		if (inv == null) return null;
		return menus.get(inv.getTitle());
	}
}
