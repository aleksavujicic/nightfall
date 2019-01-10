package deimophobe.nightfall.monster;

import deimophobe.nightfall.monster.mob.Mob;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Created by Deimophobe on 18/06/18.
 */
public interface MobCreator<T extends Mob> {
	String PERMISSION_PREFIX = "nightfall.mob.spawn.";
	String PERMISSION_POSTFIX = "";
	
	T createMob(MonsterPlayer monster);
	String getName();
	
	default Permission getPermission() {
		Permission permission = Bukkit.getPluginManager().getPermission(PERMISSION_PREFIX + getName() + PERMISSION_POSTFIX);
		if (permission != null) return permission;
		
		return new Permission(
				PERMISSION_PREFIX + getName() + PERMISSION_POSTFIX,
				"Allows the player to spawn as a " + getName() + " via commands, provided they have permission to those commands as well." ,
				PermissionDefault.TRUE
		);
	}
}
