package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeeConfig;
import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
		
		try {
			return new LobbyServer(NightfallBungeeConfig.getNBConfig().getLobbyFolder());
		} catch (IOException e) {
			plugin.getProxy().getLogger().severe("Failed to start lobby server");
			throw new IOException("Failed to start lobby server", e);
		}
	}
	
	
	public void createGameServer() {
		createGameServer((ignore) -> {});
	}
	
	public void createGameServer(Consumer<NightfallServer> onceDone) {
		proxyServer.getScheduler().runAsync(plugin, () -> {
			try {
				NightfallServer server = new NightfallServer();
				servers.put(server.getName(), server);
				onceDone.accept(server);
			} catch (IOException e) {
				e.printStackTrace();
				proxyServer.getLogger().severe("Failed to start game server.");
			}
		});
	}
	
	public void stopServer(String name) {
		MinecraftServer server = servers.get(name);
		if (server != null) server.stop();
	}
	
	/*
	
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
		}
		servers.clear();
		lobby.stop();
	}
}
