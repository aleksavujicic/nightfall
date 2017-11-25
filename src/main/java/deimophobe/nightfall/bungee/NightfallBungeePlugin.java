package deimophobe.nightfall.bungee;

import com.google.common.io.ByteStreams;
import deimophobe.nightfall.bungee.command.AddServerCommand;
import deimophobe.nightfall.bungee.command.TestCommand;
import deimophobe.nightfall.bungee.server.ServerManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * Created by Deimophobe on 14/11/17.
 */
public class NightfallBungeePlugin extends Plugin {
	
	private static NightfallBungeePlugin plugin;
	public static NightfallBungeePlugin getPlugin() { return plugin; }
	
	private ServerManager serverManager;
	public ServerManager getServerManager() { return serverManager; }
	
	private Configuration config;
	public Configuration getConfig() { return config; }
	private File rootFolder;
	private File templateFolder;
	private File runningFolder;
	public File getRootFolder() { return templateFolder; }
	public File getTemplateFolder() { return templateFolder; }
	public File getRunningFolder() { return runningFolder; }
	
	@Override
	public void onEnable() {
		plugin = this;
		
		try {
			config = loadConfig();
		} catch (IOException e) {
			e.printStackTrace();
			getProxy().stop("Failed to load Nightfall Bungee config.");
			throw new RuntimeException(e);
		}
		rootFolder = new File(System.getProperty("user.home"), config.getString("nightfall-folder",  "Nightfall"));
		templateFolder = new File(rootFolder, config.getString("template-folder", "Template"));
		runningFolder = new File(rootFolder, config.getString("run-folder", "Running"));
		
		try {
			serverManager = new ServerManager();
		} catch (Exception e) {
			e.printStackTrace();
			getProxy().getLogger().severe("Failed to start ServerManager.");
			getProxy().stop("Failed to start ServerManager.");
			throw new RuntimeException(e);
		}
		
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(this, new QueryListener());
		pm.registerCommand(this, new AddServerCommand());
		pm.registerCommand(this, new TestCommand());
	}
	
	@Override
	public void onDisable() {
		if (serverManager != null) serverManager.stopAllServers();
	}
	
	
	
	private final static String CONFIG_FILENAME = "config.yml";
	private final static boolean DEBUGGING = true;
	private Configuration loadConfig() throws IOException {
		File configFolder = this.getDataFolder();
		File configFile = new File(configFolder, CONFIG_FILENAME);
		
		if (DEBUGGING) configFile.delete();
		
		if (!configFile.exists()) {
			configFile.createNewFile();
			
			InputStream in = this.getResourceAsStream(CONFIG_FILENAME);
			OutputStream out = new FileOutputStream(configFile);
			ByteStreams.copy(in, out);
			
			out.flush();
			out.close();
			in.close();
		}
		
		return ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
	}
	
	private static final int MAX_FOLDER_NUMBER = 100;
	public File createNextFreeRunFolder(String prefix) {
		for (int i=0; i<MAX_FOLDER_NUMBER; i++) {
			File testFolder = new File(runningFolder, prefix+i);
			if (!testFolder.exists()) {
				testFolder.mkdir();
				return testFolder;
			}
		}
		throw new IllegalStateException("Too many servers with prefix '"+prefix+"' exist");
	}
}
