package deimophobe.nightfall.bungee;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 17/11/17.
 */
public class QueryListener implements Listener {
	
	private final static String RESPONSE_NAME = "Nightfall-Response";
	
	private final ListMultimap<Connection, Consumer<String>> listeners = LinkedListMultimap.create();
	
	public QueryListener() {
		ProxyServer.getInstance().registerChannel(RESPONSE_NAME);
	}
	
	@EventHandler
	public void onQueryReceive(PluginMessageEvent event) {
		if (event.getTag().equals(RESPONSE_NAME)) {
			DataInputStream di = new DataInputStream(new ByteArrayInputStream(event.getData()));
			try {
				String data = di.readUTF();
				List<Consumer<String>> consumers = listeners.get(event.getSender());
				if (consumers.size() >= 1) {
					consumers.get(0).accept(data);
					consumers.remove(0);
				} else {
					NightfallBungeePlugin.getPlugin().getLogger().warning("Dropped data: '" + data + "'");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void registerListner(Connection connection, Consumer<String> listener) {
		listeners.put(connection, listener);
	}
	
	private final Set<ProxiedPlayer> loggingPlayers = new HashSet<>();
	
	@EventHandler
	public void onPlayerPreLogin(PreLoginEvent event) {
//		if (!ServerManager.getManager().getLobby().isAlive()) {
//			event.setCancelled(true);
//			event.setCancelReason(new TextComponent("The Nightfall lobby is down - please try again later."));
//		}
	}
	
	@EventHandler
	public void onPlayerLogin(PostLoginEvent event) {
//		loggingPlayers.add(event.getPlayer());
	}
	
	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
//		boolean removed = loggingPlayers.remove(event.getPlayer());
//		if (removed)
//			event.setTarget(ServerManager.getManager().getLobby().getInfo());
	}
}
