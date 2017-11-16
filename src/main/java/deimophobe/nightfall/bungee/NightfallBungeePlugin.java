package deimophobe.nightfall.bungee;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class NightfallBungeePlugin extends Plugin {
	
	private static NightfallBungeePlugin plugin;
	public static NightfallBungeePlugin getPlugin() { return plugin; }
	
	private ServerManager serverManager;
	
	@Override
	public void onEnable() {
		plugin = this;
		serverManager = new ServerManager();
	}
	
	@Override
	public void onDisable() {
		serverManager.stopAllServers();
	}
}
