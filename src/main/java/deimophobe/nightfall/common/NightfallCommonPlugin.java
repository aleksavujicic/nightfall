package deimophobe.nightfall.common;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class NightfallCommonPlugin extends JavaPlugin {
	
	private static NightfallCommonPlugin plugin;
	public static NightfallCommonPlugin getPlugin() { return plugin; }
	
	@Override
	public void onEnable() {
		plugin = this;
	}
	
	@Override
	public void onDisable() {
	
	}
}
