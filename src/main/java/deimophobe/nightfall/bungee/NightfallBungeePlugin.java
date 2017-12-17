package deimophobe.nightfall.bungee;

import deimophobe.nightfall.bungee.command.CreateGameCommand;
import deimophobe.nightfall.bungee.command.GameListCommand;
import deimophobe.nightfall.bungee.command.TestCommand;
import deimophobe.nightfall.bungee.map.InvalidRotationConfigException;
import deimophobe.nightfall.bungee.map.MapManager;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.IOException;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class NightfallBungeePlugin extends Plugin {
	
	private static NightfallBungeePlugin plugin;
	public static NightfallBungeePlugin getPlugin() { return plugin; }
	
	private ServerManager serverManager;
	public ServerManager getServerManager() { return serverManager; }
	
	private MapManager mapManager;
	public MapManager getMapManager() { return mapManager; }
	
	private boolean shuttingDown = false;
	public boolean isShuttingDown() { return shuttingDown; }
	
	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			mapManager = new MapManager();
			mapManager.loadMapsAndRotations();
		} catch (IOException|InvalidRotationConfigException e) {
			getLogger().severe("Failed to load map manager.");
			e.printStackTrace();
			
			getProxy().stop("Failed to load map manager");
			return;
		}
		serverManager = new ServerManager();
		
		PluginManager pm = getProxy().getPluginManager();
		pm.registerCommand(this, new CreateGameCommand());
		pm.registerCommand(this, new GameListCommand());
		pm.registerCommand(this, new TestCommand());
	}
	
	@Override
	public void onDisable() {
		shuttingDown = true;
		
		if (serverManager != null)
			serverManager.onProxyStop();
	}
	
}
