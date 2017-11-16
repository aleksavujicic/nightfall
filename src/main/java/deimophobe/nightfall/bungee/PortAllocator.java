package deimophobe.nightfall.bungee;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 16/11/17.
 */
public class PortAllocator {
	private static final PortAllocator allocator = new PortAllocator();
	public static PortAllocator getAllocator() { return allocator; }
	
	private PortAllocator() {}
	
	private static final int MAX_PORT = 25700;
	private final Set<Integer> usedPorts = new HashSet<>();
	
	public int getFreePort() {
		for (int port = 25567; port < MAX_PORT; port++) {
			if (isAvailable(port)) {
				usedPorts.add(port);
				return port;
			}
		}
		throw new IllegalStateException("No free ports remaining");
	}
	
	public void releasePort(int port) {
		if (!usedPorts.contains(port)) {
			usedPorts.remove(port);
		} else {
			throw new IllegalArgumentException("Tried to release port '" + port + "' which has not been allocated?");
		}
	}
	
	
	public boolean isAvailable(int port) {
		if (usedPorts.contains(port)) return false;
		
		try (ServerSocket ignored = new ServerSocket(port)) {
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
