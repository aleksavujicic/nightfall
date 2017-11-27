package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by Deimophobe on 15/11/17.
 */
public abstract class MinecraftServer {
	
	private final ServerInfo info;
	private final ProcessBuilder builder;
	private Process serverProcess;
	private final String name;
	private final String screenName;
	private boolean alive;
	
	public ServerInfo getInfo() {
		return info;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public MinecraftServer(File serverFolder, String name, int port) throws IOException {
		this.name = name;
		this.screenName = "Nightfall-"+name;
		this.alive = false;
		
		this.builder = new ProcessBuilder();
		builder.command("screen", "-dmS", screenName, "bash", "start.sh", ""+port);
		builder.directory(serverFolder);
		this.serverProcess = builder.start();
		
		ProxyServer server = ProxyServer.getInstance();
		this.info = server.constructServerInfo(name, new InetSocketAddress(port), "Temp", false);
		
		checkAlive(((result, error) -> {
			alive = true;
			server.getServers().put(name, info);
		}));
	}
	
	private ScheduledTask lifeCheckerTask = null;
	
	public void checkAlive() {
		checkAlive(((result, error) -> {}));
	}
	
	public void checkAlive(Callback<ServerPing> whenAlive) {
		if (lifeCheckerTask != null) {
			NightfallBungeePlugin.getPlugin().getLogger().warning("Tried to check life of server being checked: "+ name);
			return;
		}
		
		Runnable checker = new Runnable() {
			private int tries = 20;
			
			@Override
			public void run() {
				info.ping((result, error) -> {
					if (result == null) {
						tries--;
						
						// Failed to find server
						if (tries == 0) {
							failedToCheckAlive();
							lifeCheckerTask.cancel();
							lifeCheckerTask = null;
						}
					} else {
						// Server is alive
						lifeCheckerTask.cancel();
						lifeCheckerTask = null;
						
						whenAlive.done(result, error);
					}
				});
			}
		};
		
		lifeCheckerTask = ProxyServer.getInstance().getScheduler().schedule(NightfallBungeePlugin.getPlugin(), checker, 10, 3, TimeUnit.SECONDS);
	}
	
	private void failedToCheckAlive() {
		NightfallBungeePlugin.getPlugin().getLogger().warning("Server is not alive, forcefully stopping: "+ name);
		stop();
	}
	
	public void stop() {
		serverProcess.destroy();
		
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec("screen -X -S " + screenName + " quit");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ProxyServer.getInstance().getServers().remove(name);
		alive = false;
	}
	
	/*
	
	
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
	
	public void sendRawData(String channel, byte[] data) {
		info.sendData(channel, data);
	}
	
	public Process getProcess() {
		return serverProcess;
	}
	*/
}
