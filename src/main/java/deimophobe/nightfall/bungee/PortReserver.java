package deimophobe.nightfall.bungee;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 16/11/17.
 */
public class PortReserver {
	private static final PortReserver allocator = new PortReserver();
	public static PortReserver getReserver() { return allocator; }
	
	private PortReserver() {}
	
	private static final int MAX_PORT = 25700;
	private final Set<Integer> usedPorts = new HashSet<>();
	
	public int findFreePort() {
		for (int port = 25567; port < MAX_PORT; port++) {
			if (isAvailable(port)) {
				return port;
			}
		}
		throw new IllegalStateException("No free ports remaining");
	}
	
	public void reservePort(int port) {
		if (isAvailable(port)) {
			usedPorts.add(port);
		} else {
			throw new IllegalArgumentException("Tried to reserve port '" + port + "' which is not available?");
		}
	}
	
	public void releasePort(int port) {
		if (!usedPorts.contains(port)) {
			usedPorts.remove(port);
		} else {
			throw new IllegalArgumentException("Tried to release port '" + port + "' which has not been reserved?");
		}
	}
	
	
	public boolean isAvailable(int port) {
		if (usedPorts.contains(port)) return false;
		
		try (ServerSocket ignored = new ServerSocket(port)) {
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
