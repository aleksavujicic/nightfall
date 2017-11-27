package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.ProxyServer;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class LobbyServer extends MinecraftServer {
	public LobbyServer(File serverFolder) throws IOException {
		super(serverFolder, "lobby", 25564);
	}
	
	@Override
	public void stop() {
		super.stop();
		
		if (!NightfallBungeePlugin.getPlugin().isShuttingDown()) {
			NightfallBungeePlugin.getPlugin().getLogger().severe("Lobby server stopped.");
			ProxyServer.getInstance().stop("Lobby server failed to be alive");
		}
	}
}
