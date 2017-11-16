package deimophobe.nightfall.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class LobbyServer extends MinecraftServer {
	public LobbyServer(File serverFolder, String serverJar) {
		super(serverFolder, serverJar);
	}
	
	@Override
	public String getName() {
		return "lobby2";
	}
	
	@Override
	public ServerInfo getInfo() {
		return ProxyServer.getInstance().constructServerInfo(getName(), new InetSocketAddress(25566), "blah", false);
	}
}
