package deimophobe.nightfall.bungee;

import deimophobe.nightfall.bungee.command.AddServerCommand;
import deimophobe.nightfall.bungee.command.TestCommand;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class NightfallBungeePlugin extends Plugin {
	
	private static NightfallBungeePlugin plugin;
	public static NightfallBungeePlugin getPlugin() { return plugin; }
	
	private NightfallBungeeConfig config;
	public NightfallBungeeConfig getConfig() { return config; }
	
	private ServerManager serverManager;
	public ServerManager getServerManager() { return serverManager; }
	
	private boolean shuttingDown = false;
	public boolean isShuttingDown() { return shuttingDown; }
	
	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
			getProxy().stop("Failed to load Nightfall Bungee config.");
			throw new RuntimeException(e);
		}
		
		try {
			serverManager = new ServerManager();
		} catch (Exception e) {
			e.printStackTrace();
			getProxy().getLogger().severe("Failed to start ServerManager.");
			getProxy().stop("Failed to start ServerManager.");
			throw new RuntimeException(e);
		}
		
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(this, new QueryListener());
		pm.registerCommand(this, new AddServerCommand());
		pm.registerCommand(this, new TestCommand());
	}
	
	@Override
	public void onDisable() {
		shuttingDown = true;
		if (serverManager != null) serverManager.stopAllServers();
	}
	
	public void loadConfig() throws IOException {
		this.config = new NightfallBungeeConfig(this);
	}
	
}
