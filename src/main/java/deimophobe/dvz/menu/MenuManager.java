package deimophobe.dvz.menu;

import deimophobe.dvz.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MenuManager {
	private static MenuManager ourManager = new MenuManager();
	static MenuManager getManager() {
		return ourManager;
	}
	
	private final Map<Player, MenuSession<?>> activeSessions = new HashMap<>();
	
	private MenuManager() {
		Bukkit.getPluginManager().registerEvents(new MenuListener(), Game.getGame().getPlugin());
	}
	
	public <T extends SessionData> MenuSession<T> startSession(MainMenu<T> mainMenu, Player player) {
		MenuSession<T> session = new MenuSession<>(mainMenu, player);
		activeSessions.put(player, session);
		return session;
	}
	
	MenuSession<?> getSession(Player player) {
		return activeSessions.get(player);
	}
	
	void closeSession(Player player) {
		MenuSession<?> session = getSession(player);
		if (session != null) {
			activeSessions.remove(session.getPlayer());
			session.onClose();
		}
	}
}
