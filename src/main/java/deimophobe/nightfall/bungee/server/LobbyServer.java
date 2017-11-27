package deimophobe.nightfall.bungee.server;

import java.io.File;
import java.io.IOException;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class LobbyServer extends MinecraftServer {
	public LobbyServer(File serverFolder, String jarName) throws IOException {
		super(serverFolder, jarName, "lobby", 25564);
	}
	
	@Override
	public void stop() {
		super.stop();
		
	}
}
