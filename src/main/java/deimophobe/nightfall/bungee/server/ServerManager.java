package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.io.File;
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
	
	public LobbyServer getLobby() {
		return lobby;
	}
	
	private final ProxyServer proxyServer;
	
	public ServerManager() throws IOException {
		this.plugin = NightfallBungeePlugin.getPlugin();
		proxyServer = plugin.getProxy();
		
		this.lobby = createLobby();
		
	}
	
	private LobbyServer createLobby() throws IOException {
		NightfallBungeePlugin plugin = NightfallBungeePlugin.getPlugin();
		Configuration config = plugin.getConfig();
		
		String jarName = config.getString("lobby.jar", "spigot-1.12.jar");
		String srcName = config.getString("lobby.src-folder");
		File srcFolder = new File(plugin.getRunningFolder(), srcName);
		
		try {
			return new LobbyServer(srcFolder, jarName);
		} catch (IOException e) {
			plugin.getProxy().getLogger().severe("Failed to start lobby server");
			throw new IOException("Failed to start lobby server", e);
		}
	}
	
	/*
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
	*/
	
	public void stopAllServers() {
		for (MinecraftServer server : servers.values()) {
			server.stop();
			//proxyServer.getServers().remove(server.getInfo().getName());
		}
		servers.clear();
		lobby.stop();
	}
}
