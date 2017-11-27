package deimophobe.nightfall.bungee;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * Created by Deimophobe on 27/11/17.
 */
public class NightfallBungeeConfig {
	
	public static NightfallBungeeConfig getNBConfig() {
		return NightfallBungeePlugin.getPlugin().getConfig();
	}
	
	private final Configuration config;
	public Configuration getConfig() { return config; }
	
	public NightfallBungeeConfig(NightfallBungeePlugin plugin) throws IOException {
		this.config = loadConfig(plugin);
		
		rootFolder = new File(System.getProperty("user.home"), config.getString("nightfall-folder",  "Nightfall"));
		templateFolder = new File(rootFolder, config.getString("template-folder", "Template"));
		runningFolder = new File(rootFolder, config.getString("run-folder", "Running"));
		lobbyFolder = new File(rootFolder, config.getString("lobby-folder", "Lobby"));
	}
	
	private final File rootFolder;
	private final File templateFolder;
	private final File runningFolder;
	private final File lobbyFolder;
	public File getRootFolder() { return templateFolder; }
	public File getTemplateFolder() { return templateFolder; }
	public File getRunningFolder() { return runningFolder; }
	public File getLobbyFolder() { return lobbyFolder; }
	
	private final static String CONFIG_FILENAME = "config.yml";
	private final static boolean DEBUGGING = true;
	private Configuration loadConfig(NightfallBungeePlugin plugin) throws IOException {
		File configFolder = plugin.getDataFolder();
		File configFile = new File(configFolder, CONFIG_FILENAME);
		
		if (DEBUGGING) configFile.delete();
		
		if (!configFile.exists()) {
			configFile.createNewFile();
			
			InputStream in = plugin.getResourceAsStream(CONFIG_FILENAME);
			OutputStream out = new FileOutputStream(configFile);
			ByteStreams.copy(in, out);
			
			out.flush();
			out.close();
			in.close();
		}
		
		return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
	}
}
