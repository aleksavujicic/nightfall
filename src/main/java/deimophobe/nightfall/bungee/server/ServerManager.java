package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.map.GameMap;
import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import deimophobe.nightfall.bungee.map.MapManager;
import net.ME1312.SubServers.Bungee.Host.Server;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.ME1312.SubServers.Bungee.SubAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class ServerManager {
	public static ServerManager getManager() { return NightfallBungeePlugin.getPlugin().getServerManager(); }
	private final NightfallBungeePlugin plugin;
	private final ProxyServer proxyServer;
	
	public static final String GAME_GROUP_NAME = "Nightfall-Game";
	private final Queue<Game> queuedGames;
	private final Map<SubServer, Game> activeGames;
	
	public SubServer getLobby() { return SubAPI.getInstance().getSubServer("Lobby"); }
	public boolean isLobbyRunning() {
		SubServer lobby = getLobby();
		return (lobby != null && lobby.isRunning());
	}
	
	
	public ServerManager() {
		this.plugin = NightfallBungeePlugin.getPlugin();
		this.proxyServer = plugin.getProxy();
		proxyServer.getPluginManager().registerListener(plugin, new ServerListener());
		
		this.queuedGames = new ConcurrentLinkedQueue<>();
		this.activeGames = new HashMap<>();
	}
	
	public void createGame(GameMap map) {
		queuedGames.add(new Game(map));
		flushQueuedGames();
	}
	
	public void flushQueuedGames() {
		if (queuedGames.size() == 0) return;
		
		proxyServer.getScheduler().runAsync(plugin, () -> {
			synchronized (ServerManager.this) {
				while (true) {
					SubServer server = getFreeGameServer();
					if (server == null) break;
					
					Game game = queuedGames.poll();
					if (game == null) break;
					
					try {
						game.start(server);
						activeGames.put(server, game);
					} catch (Exception e) {
						plugin.getLogger().severe("Failed to start game.");
						e.printStackTrace();
						
						game.forceStop("Failed to start");
						
						// Re add to queue if it fails
						queuedGames.add(new Game(game));
					}
					
					// Wait 10s
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						NightfallBungeePlugin.getPlugin().getLogger().severe("Interrupted game flushing");
						break;
					}
				}
			}
		});
	}
	
	void onServerStop(SubServer server) {
		Game stoppedGame = activeGames.remove(server);
		if (stoppedGame == null) {
			plugin.getLogger().warning("Game server stopped with no game? Server: " + server.getName());
		} else {
			stoppedGame.notifyStop();
		}
		flushQueuedGames();
		checkToAddRotationMap();
	}
	
	private void checkToAddRotationMap() {
		if (!queuedGames.isEmpty() || !activeGames.isEmpty()) return;
		if (NightfallBungeePlugin.getPlugin().isShuttingDown()) return;
		createGame(MapManager.getManager().getNextRotationMap());
	}
	
	public void onProxyStop() {
		queuedGames.clear();
		for (Game game : activeGames.values()) {
			game.forceStop("Server shutting down");
		}
		activeGames.clear();
	}
	
	private SubServer getFreeGameServer() {
		List<Server> servers = SubAPI.getInstance().getGroup(GAME_GROUP_NAME);
		if (servers == null) return null;
		
		for (Server server : servers) {
			if (server instanceof SubServer) {
				SubServer subServer = (SubServer) server;
				if (!subServer.isRunning() && !activeGames.containsKey(subServer)) {
					return (SubServer) server;
				}
			}
		}
		return null;
	}
	
	public String getGameList() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GOLD + "Queued Games:");
		for (Game game : queuedGames) {
			sb.append("\n - ").append(game.toString());
		}
		
		sb.append("\n" + ChatColor.GOLD + "Active Games:");
		for (Game game : activeGames.values()) {
			sb.append("\n - ").append(game.toString());
		}
		
		return sb.toString();
	}
}
