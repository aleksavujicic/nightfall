package deimophobe.nightfall.bungee.server;

import net.ME1312.SubServers.Bungee.Event.SubStoppedEvent;
import net.ME1312.SubServers.Bungee.Host.SubServer;
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
}
