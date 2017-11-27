package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeeConfig;
import deimophobe.nightfall.bungee.NightfallBungeePlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

import java.io.File;

/**
 * Created by Deimophobe on 16/11/17.
 */
public enum ServerType {
	NIGHTFALL("nightfall-game"),
	MAP_EDITOR("map-editor"),
	
	;
	
	private final File srcFolder;
	private final String runPrefix;
	
	public File getSrcFolder() {
		return srcFolder;
	}
	
	public String getRunPrefix() {
		return runPrefix;
	}
	
	ServerType(String configName) {
		NightfallBungeeConfig nbConfig = NightfallBungeeConfig.getNBConfig();
		Configuration config = nbConfig.getConfig();
		
		if (!config.contains(configName)) {
			NightfallBungeePlugin.getPlugin().getLogger().severe("Failed to find config for template: " + name());
			ProxyServer.getInstance().stop("Failed to find config for template: " + name());
		}
		
		runPrefix = config.getString(configName + ".run-folder-prefix", null);
		String srcName = config.getString(configName + ".src-folder");
		srcFolder = new File(nbConfig.getTemplateFolder(), srcName);
	}
}
