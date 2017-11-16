package deimophobe.nightfall.bungee;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 15/11/17.
 */
public abstract class MinecraftServer implements NfServer {
	
	private int port;
	
	private final ProcessBuilder builder;
	private Process serverProcess = null;
	
	public MinecraftServer(File serverFolder, String serverJar) {
		this.builder = new ProcessBuilder();
		//builder.command("screen", "-dmS","Nightfall","java","-jar",serverJar).directory(serverFolder);
		builder.command("java","-jar",serverJar).directory(serverFolder);
	}
	
	public void setPort() {
		//if ()
	}
	
	@Override
	public void start() {
		if (isRunning()) throw new IllegalStateException("Tried to start server which has already started.");
		
		try {
			serverProcess = builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isRunning() {
		return (serverProcess != null);
	}
	
	@Override
	public void stop() {
		if (!isRunning()) throw new IllegalStateException("Tried to stop server which hasn't started.");
		
		serverProcess.destroy();
	}
}
