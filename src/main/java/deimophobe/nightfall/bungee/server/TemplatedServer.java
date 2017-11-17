package deimophobe.nightfall.bungee.server;

import java.io.IOException;

/**
 * Created by Deimophobe on 16/11/17.
 */
public abstract class TemplatedServer extends MinecraftServer {
	public TemplatedServer(ServerType type, ServerSettings settings) throws IOException {
		super(type, settings);
	}
}
