package deimophobe.nightfall.common.menu;

import deimophobe.nightfall.common.Misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 1/05/17.
 */
public class MenuManager {
	private static MenuManager ourManager = null;
	static MenuManager getManager() {
		return ourManager;
	}
	public static void initialiseMenuManager(Plugin plugin) {
		if (ourManager != null) {
			throw new IllegalStateException("Tried to initialise MenuManager but it had already been initialised.");
		} else {
			ourManager = new MenuManager(plugin);
		}
	}
	
	private final Map<Player, MenuSession<?>> activeSessions = new HashMap<>();
	
	private MenuManager(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
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
