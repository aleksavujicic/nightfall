package deimophobe.nightfall.monster;

import deimophobe.nightfall.monster.mob.Mob;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Created by Deimophobe on 18/06/18.
 */
public interface MobCreator<T extends Mob> {
	String PERMISSION_PREFIX = "nightfall.mob.";
	String PERMISSION_POSTFIX = ".spawn";
	
	T createMob(MonsterPlayer monster);
	String getName();
	
	default Permission getPermission() {
		return new Permission(PERMISSION_PREFIX + getName() + PERMISSION_POSTFIX, PermissionDefault.TRUE);
	}
}
