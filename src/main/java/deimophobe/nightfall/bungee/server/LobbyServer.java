package deimophobe.nightfall.bungee.server;

import java.io.IOException;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class LobbyServer extends MinecraftServer {
	public LobbyServer(ServerSettings settings) throws IOException {
		super(ServerType.LOBBY, settings);
	}
}
