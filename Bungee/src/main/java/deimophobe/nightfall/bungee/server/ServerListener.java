package deimophobe.nightfall.bungee.server;

import net.ME1312.SubServers.Bungee.Event.SubStoppedEvent;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Deimophobe on 13/12/17.
 */
public class ServerListener implements Listener {
	
	@EventHandler
	public void onServerStop(SubStoppedEvent event) {
		SubServer server = event.getServer();
		if (server.getGroups().contains(ServerManager.GAME_GROUP_NAME)) {
			ServerManager.getManager().onServerStop(server);
		}
	}
	
	@EventHandler
	public void onPlayerPreLogin(PreLoginEvent event) {
		ServerManager sm = ServerManager.getManager();
		if (!sm.isLobbyRunning()) {
			event.setCancelled(true);
			event.setCancelReason(new TextComponent("The Nightfall lobby is down - please try again later."));
		}
	}
	
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		
		if (event.getReason() == ServerConnectEvent.Reason.JOIN_PROXY) {
			ServerManager sm = ServerManager.getManager();
			if (sm.isLobbyRunning()) {
				event.setTarget(sm.getLobby());
			} else {
				event.setCancelled(true);
				player.disconnect(new TextComponent("Failed to connect to the Lobby"));
			}
		}
	}
	
//	@EventHandler
//	public void onServerEnd(SubStopEvent event) {
//		SubServer lobby = ServerManager.getManager().getLobby();
//		for (ProxiedPlayer player : event.getServer().getPlayers()) {
//			if (lobby != null && lobby.isRunning()) {
//				player.connect(lobby);
//			} else {
//				player.disconnect(new TextComponent("Failed to reconnect to Lobby"));
//			}
//		}
//	}

	@EventHandler
	public void onKick(ServerKickEvent event) {
		ProxiedPlayer player = event.getPlayer();
		ServerManager sm = ServerManager.getManager();
		if (sm.isLobbyRunning()) {
			SubServer lobby = sm.getLobby();
			ServerInfo kickFrom = event.getKickedFrom();
			// If kicked from lobby, do nothing
			if (lobby.getName().equals(kickFrom.getName())) return;
			
			// Cancel kick, player will be sent to fallback server (Lobby)
			event.setCancelled(true);
		} else {
			player.disconnect(new TextComponent("Failed to reconnect to the Lobby"));
		}
	}
}
