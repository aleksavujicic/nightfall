package deimophobe.dvz.menu;

import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MainMenu<T extends SessionData> extends SubMenu<T> {
	String getTitle();
	T getDataFromPlayer(Player player);
	
	default void startSession(Player player) {
		MenuManager.getManager().startSession(this, player);
	}
}
