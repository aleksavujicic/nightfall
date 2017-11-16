package deimophobe.nightfall.bungee;

import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created by Deimophobe on 14/11/17.
 */
public interface NfServer {
	
	String getName();
	ServerInfo getInfo();
	
	void start();
	void stop();
	boolean isRunning();
}
