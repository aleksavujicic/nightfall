package deimophobe.nightfall.common.menu;

import deimophobe.nightfall.common.menu.submenu.SubMenu;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MainMenu<T extends SessionData> extends SubMenu<T> {
	String getTitle();
	T getDataFromPlayer(Player player);
	String getMenuPermission();
	
	default void startSession(Player player) {
		MenuManager.getManager().startSession(this, player);
	}
}
