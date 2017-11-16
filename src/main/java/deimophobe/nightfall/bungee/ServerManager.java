package deimophobe.nightfall.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class ServerManager {
	private final Map<String, NfServer> servers = new HashMap<>();
	private final LobbyServer lobby;
	
	private final ProxyServer proxyServer;
	
	private final static String CONFIG_FILENAME = "config.yml";
	private final static boolean DEBUGGING = true;
	
	public ServerManager() {
		Plugin plugin = NightfallBungeePlugin.getPlugin();
		proxyServer = plugin.getProxy();
		
		Configuration config;
		try {
			config = loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
			proxyServer.stop("Failed to load Nightfall config.");
			throw new RuntimeException(e);
		}
		
		
		// Bit of a hack but meh
		File serverFolder = proxyServer.getPluginsFolder().getParentFile();
		File nightfallFolder = new File(serverFolder, config.getString("nightfall-folder", ".."));
		
		File lobbyFolder = new File(nightfallFolder, config.getString("lobby.folder", "Lobby"));
		String lobbyJar = config.getString("lobby.jar", "spigot-1.12.jar");
		
		lobby = new LobbyServer(lobbyFolder, lobbyJar);
		startServer(lobby);
	}
	
	private Configuration loadConfig() throws IOException {
		Plugin plugin = NightfallBungeePlugin.getPlugin();
		
		File configFolder = plugin.getDataFolder();
		File configFile = new File(configFolder, CONFIG_FILENAME);
		
		if (DEBUGGING) configFile.delete();
		
		if (!configFile.exists()) {
			configFile.createNewFile();
			
			InputStream in = plugin.getResourceAsStream(CONFIG_FILENAME);
			OutputStream out = new FileOutputStream(configFile);
			ByteStreams.copy(in, out);
			
			out.flush();
			out.close();
			in.close();
		}
		
		return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
	}
	
	public void startServer(NfServer server) {
		server.start();
		servers.put(server.getName(), server);
		proxyServer.getServers().put(server.getName(), server.getInfo());
	}
	
	public void stopServer(NfServer server) {
		server.stop();
		servers.remove(server.getName());
		proxyServer.getServers().remove(server.getName());
	}
	
	public void stopAllServers() {
		for (NfServer server : servers.values()) {
			server.stop();
			proxyServer.getServers().remove(server.getName());
		}
		servers.clear();
	}
}
