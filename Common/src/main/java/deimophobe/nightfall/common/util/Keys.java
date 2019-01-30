package deimophobe.nightfall.common.util;


import deimophobe.nightfall.common.NightfallCommonPlugin;
import org.bukkit.NamespacedKey;

import java.security.Key;

/**
 * Created by Deimophobe on 24/01/19.
 */
public final class Keys {
	private Keys() {}
	
	public static final NamespacedKey BOW_POWER_KEY = newNightfallKey("power");
	
	
	private static NamespacedKey newNightfallKey(String key) {
		return new NamespacedKey(NightfallCommonPlugin.getPlugin(), key);
	}
}
