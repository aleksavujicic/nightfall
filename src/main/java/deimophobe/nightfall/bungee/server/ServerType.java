package deimophobe.nightfall.bungee.server;

import deimophobe.nightfall.bungee.NightfallBungeePlugin;

import net.md_5.bungee.config.Configuration;
import java.io.File;

/**
 * Created by Deimophobe on 16/11/17.
 */
public enum ServerType {
	LOBBY("lobby"),
	NIGHTFALL("nightfall")
	
	;
	
	private final String jarName;
	private final File srcFolder;
	private final String runPrefix;
	
	public String getJarName() {
		return jarName;
	}
	
	public File getSrcFolder() {
		return srcFolder;
	}
	
	public String getRunPrefix() {
		return runPrefix;
	}
	
	public boolean isInPlace() {
		return runPrefix == null;
	}
	
	ServerType(String configName) {
		NightfallBungeePlugin plugin = NightfallBungeePlugin.getPlugin();
		Configuration config = plugin.getConfig();
		
		jarName = config.getString(configName + ".jar", "spigot-1.12.jar");
		runPrefix = config.getString(configName + ".run-folder-prefix", null);
		
		String srcName = config.getString(configName + ".src-folder");
		
		if (isInPlace()) {
			srcFolder = new File(plugin.getRunningFolder(), srcName);
		} else {
			srcFolder = new File(plugin.getTemplateFolder(), srcName);
		}
	}
}
