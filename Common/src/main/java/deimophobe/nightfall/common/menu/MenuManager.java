package deimophobe.nightfall.common.menu;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 1/05/17.
 */
public final class MenuManager {
	public static MenuManager getManager() {
		return NightfallCommonPlugin.getPlugin().getMenuManager();
	}
	
	private final Map<Player, MenuSession<?>> activeSessions = new HashMap<>();
	private final ClassToInstanceMap<MainMenu<?>> registeredMenus;
	
	public MenuManager(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(new MenuListener(), plugin);
		
		registeredMenus = MutableClassToInstanceMap.create();
	}
	
	// ---------- Menu Registering ----------
	
	public <S extends MainMenu<?>> void registerMenu(Class<S> menuClass, S menu) {
		checkNotNull(menuClass, "Menu class must not be null.");
		checkNotNull(menu, "Menu must not be null.");
		
		registeredMenus.putInstance(menuClass, menu);
		
		Permission menuPerm = menu.getPermission();
		Bukkit.getPluginManager().addPermission(menuPerm);
	}
	
	public <S extends MainMenu<?>> S getMenu(Class<S> menuClass) {
		return registeredMenus.getInstance(menuClass);
	}
	
	
	// ---------- Session Management ----------
	
	public <T extends SessionData> void startSession(Class<? extends MainMenu<T>> menuClass, Player player) {
		checkNotNull(menuClass, "Menu class must not be null.");
		checkArgument(registeredMenus.containsKey(menuClass), "Menu '%s' must be registered before starting a session.", menuClass.getSimpleName());
		
		MainMenu<T> menu = registeredMenus.getInstance(menuClass);
		startSession(menu, player);
	}
	
	public <T extends SessionData> void startSession(MainMenu<T> mainMenu, Player player) {
		checkNotNull(mainMenu, "Menu must not be null.");
		checkNotNull(player, "Player must not be null.");
		
		Permission perm = mainMenu.getPermission();
		if (!player.hasPermission(perm)) {
			player.sendMessage(ChatColor.RED + "You do not have permission to open that menu.");
			return;
		}
		
		forceCloseAnyOpenSession(player, mainMenu);
		
		MenuSession<T> session = new MenuSession<>(mainMenu, player, null);
		activeSessions.put(player, session);
	}
	
	void setSession(MenuSession<?> session) {
		checkNotNull(session, "Session must not be null.");
		
		Player player = session.getPlayer();
		activeSessions.put(player, session);
	}
	
	private void forceCloseAnyOpenSession(Player player, MainMenu<?> openingMenu) {
		MenuSession<?> session = getSession(player);
		if (session != null) {
			session.closeSession();
			
			String sessionName = session.getMenu().getClass().getSimpleName();
			String menuName = openingMenu.getClass().getSimpleName();
			String playerName = player.getName();
			NightfallCommonPlugin.logger().warning("Force closed session from '" + sessionName + "' while opening '" + menuName + "' for player '" + playerName);
		}
	}
	
	public boolean hasOpenSession(Player player) {
		return activeSessions.containsKey(player);
	}
	
	public boolean hasOpenSession(Class<? extends MainMenu<?>> menuClass, Player player) {
		MainMenu<?> menu = getMenu(menuClass);
		return hasOpenSession(menu, player);
	}
	
	public boolean hasOpenSession(MainMenu<?> menu, Player player) {
		MenuSession<?> session = activeSessions.get(player);
		if (session == null) return false;
		
		return session.isMenu(menu);
	}
	
	MenuSession<?> getSession(Player player) {
		return activeSessions.get(player);
	}
	
	void closeSession(Player player) {
		MenuSession<?> session = getSession(player);
		if (session != null) {
			activeSessions.remove(player);
			session.onClose();
		}
	}
}
