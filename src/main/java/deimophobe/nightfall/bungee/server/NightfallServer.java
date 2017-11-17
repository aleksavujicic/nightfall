package deimophobe.nightfall.bungee.server;

import java.io.IOException;

/**
 * Created by Deimophobe on 16/11/17.
 */
public class NightfallServer extends TemplatedServer {
	public NightfallServer(NightfallServerSettings settings) throws IOException {
		super(ServerType.NIGHTFALL, settings);
	}
}
