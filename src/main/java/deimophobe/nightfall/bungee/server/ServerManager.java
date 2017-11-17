package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class ServerManager {
	public static ServerManager getManager() { return NightfallBungeePlugin.getPlugin().getServerManager(); }
	private final NightfallBungeePlugin plugin;
	
	private final Map<String, MinecraftServer> servers = new HashMap<>();
	private final LobbyServer lobby;
	
	private final ProxyServer proxyServer;
	
	public ServerManager() throws IOException {
		this.plugin = NightfallBungeePlugin.getPlugin();
		proxyServer = plugin.getProxy();
		
		ServerSettings lobbySettings = new ServerSettings();
		lobbySettings.setPort(25564);
		try {
			lobby = new LobbyServer(lobbySettings);
		} catch (IOException e) {
			plugin.getProxy().getLogger().severe("Failed to start lobby server");
			throw new IOException("Failed to start lobby server", e);
		}
	}
	
	public MinecraftServer getLobby() {
		return lobby;
	}
	
	public void create(MinecraftServer server) {
		
		ServerInfo info = server.getInfo();
		String name = info.getName();
		
		servers.put(name, server);
		proxyServer.getServers().put(name, info);
	}
	
	public void stopServer(MinecraftServer server) {
		server.stop();
		
		String name = server.getInfo().getName();
		
		servers.remove(name);
		proxyServer.getServers().remove(name);
	}
	
	public void stopAllServers() {
		for (MinecraftServer server : servers.values()) {
			server.stop();
			proxyServer.getServers().remove(server.getInfo().getName());
		}
		servers.clear();
	}
}
