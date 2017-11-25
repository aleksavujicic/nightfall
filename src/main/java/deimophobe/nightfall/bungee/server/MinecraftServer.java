package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import deimophobe.nightfall.bungee.PortReserver;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Deimophobe on 15/11/17.
 */
public abstract class MinecraftServer {
	
	private final ServerType type;
	
	private final ServerInfo info;
	
	private final Process serverProcess;
	private final String screenName;
	private final int port;
	
	private final String displayName;
	private final ChatColor color;
	
	public MinecraftServer(ServerType type, ServerSettings settings) throws IOException {
		this.type = type;
		
		PortReserver reserver = PortReserver.getReserver();
		int port = settings.getPort();
		if (port == -1)
			port = reserver.findFreePort();
		
		this.port = port;
		reserver.reservePort(port);
		
		File serverFolder = createServerFolder();
		String internalName = serverFolder.getName();
		ProcessBuilder builder = new ProcessBuilder();
		screenName = "Nightfall-"+internalName;
		builder.command("screen", "-dmS", screenName, "java", "-jar", type.getJarName(), "--port", ""+port);
		//builder.command("java", "-jar", type.getJarName(), "--port", ""+port);
		builder.directory(serverFolder);
		serverProcess = builder.start();
		
		info = ProxyServer.getInstance().constructServerInfo(
				internalName,
				new InetSocketAddress(port),
				settings.getMotd(),
				settings.isRestricted());
		
		this.displayName = settings.getDisplayName();
		this.color = settings.getColour();
	}
	
	protected File createServerFolder() throws IOException {
		File serverFolder;
		if (type.isInPlace()) {
			serverFolder = type.getSrcFolder();
		} else {
			File sourceFolder = type.getSrcFolder();
			serverFolder = NightfallBungeePlugin.getPlugin().createNextFreeRunFolder(type.getRunPrefix());
			
			FileUtils.copyDirectory(sourceFolder, serverFolder);
		}
		return serverFolder;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public ServerInfo getInfo() {
		return info;
	}
	
	public boolean isAlive() {
		return serverProcess.isAlive();
	}
	
	public void stop() {
		if (!isAlive()) throw new IllegalStateException("Tried to stop server which is dead.");
		
		PortReserver.getReserver().releasePort(port);
		serverProcess.destroyForcibly();
		
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("screen -X -S " + screenName + " quit");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRawData(String channel, byte[] data) {
		info.sendData(channel, data);
	}
	
	public Process getProcess() {
		return serverProcess;
	}
}
