package deimophobe.nightfall.bungee;

import deimophobe.nightfall.bungee.command.CommandInitialiserUtil;
import deimophobe.nightfall.bungee.command.TestCommand;
import deimophobe.nightfall.bungee.map.InvalidRotationConfigException;
import deimophobe.nightfall.bungee.map.MapManager;
import deimophobe.nightfall.bungee.packet.GameCreatePacketOut;
import deimophobe.nightfall.bungee.packet.GameEndPacketOut;
import deimophobe.nightfall.bungee.packet.GameMessenger;
import deimophobe.nightfall.bungee.packet.GameStartPacketOut;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.ME1312.SubServers.Bungee.Network.SubDataServer;
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
	public boolean isShuttingDown() { return shuttingDown; } // TODO Doesn't get triggered early enough?
	
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
		pm.registerCommand(this, new TestCommand());
		
		pm.registerListener(this, new GameMessenger());
		SubDataServer.registerPacket( GameCreatePacketOut.class, GameCreatePacketOut.handle() );
		SubDataServer.registerPacket( GameStartPacketOut.class,  GameStartPacketOut.handle()  );
		SubDataServer.registerPacket( GameEndPacketOut.class,    GameEndPacketOut.handle()    );
		
		CommandInitialiserUtil.initialiseCommands(this);
	}
	
	@Override
	public void onDisable() {
		shuttingDown = true;
		
		if (serverManager != null)
			serverManager.onProxyStop();
	}
	
}
