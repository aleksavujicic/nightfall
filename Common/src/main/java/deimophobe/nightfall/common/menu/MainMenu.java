package deimophobe.nightfall.common.menu;

import deimophobe.nightfall.common.menu.submenu.SubMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Created by Deimophobe on 2/02/17.
 */
public interface MainMenu<T extends SessionData> extends SubMenu<T> {
	String PERMISSION_PREFIX = "nightfall.menu.";
	String PERMISSION_POSTFIX = "";
	
	String getTitle();
	T getDataFromPlayer(Player player);
	String getPermissionName();
	
	
	default Permission getPermission() {
		String permissionName = PERMISSION_PREFIX + getPermissionName() + PERMISSION_POSTFIX;
		
		Permission permission = Bukkit.getPluginManager().getPermission(permissionName);
		if (permission != null) return permission;
		
		return new Permission(
				permissionName,
				PermissionDefault.TRUE
		);
	}
	
	default void startSession(Player player) {
		MenuManager.getManager().startSession(this, player);
	}
}
