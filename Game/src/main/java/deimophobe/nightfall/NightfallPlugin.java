package deimophobe.nightfall;

import deimophobe.nightfall.command.CommandInitialiserUtil;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.game.Game;
import deimophobe.nightfall.game.GameListener;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class NightfallPlugin extends JavaPlugin {
	
	private static NightfallPlugin plugin;
	private GameListener gl;
	
	public static NightfallPlugin getPlugin() {return plugin;}
	public static Logger logger() { return plugin.getLogger(); }
	
	private boolean disabling = false;
	public boolean isDisabling() { return disabling; }
	
	public static void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		
		// Check dependencies exist
		try {
			checkDependency("Nightfall Common", "deimophobe.nightfall.common.NightfallCommonPlugin");
			checkDependency("ProtocolLib", "me.libraryaddict.disguise.LibsDisguises");
			checkDependency("Lib's Disguises", "com.comphenix.protocol.ProtocolLib");
			checkDependency("Packet Wrapper", "com.comphenix.packetwrapper.AbstractPacket");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			getLogger().severe("Could not load all dependencies, disabling.");
			plugin.getPluginLoader().disablePlugin(this);
			return;
		}
		
		//cleanPlayerDataFiles();
		
		PacketUtil.setupListeners();
		
		gl = new GameListener();
		Game.createNewGame();
		Bukkit.getPluginManager().registerEvents(gl, NightfallPlugin.getPlugin());
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		
		initialiseMenus();
		
		CommandInitialiserUtil.initialiseCommands(this);
	}
	
	@Override
	public void onDisable() {
		disabling = true;
		
		Game game = Game.getGame();
		if (game != null) game.stop();
	}
	
	public void updateManagers() {
		gl.updateManagers();
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
	
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new VoidChunkGenerator();
	}
	
	
	private void cleanPlayerDataFiles() {
		World world = Bukkit.getWorlds().get(0);
		File playerDataFolder = new File(world.getWorldFolder(), "playerdata");
		for (File file : playerDataFolder.listFiles()) {
			file.delete();
		}
	}
	
	private void checkDependency(String name, String clazz) throws ClassNotFoundException {
		try {
			Class.forName(clazz, false, this.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new ClassNotFoundException("Unknown dependency '" + name + "'. Class " + clazz + " could not be found.", e);
		}
	}
	
	private void initialiseMenus() {
		MenuManager manager = MenuManager.getManager();
		
		manager.registerMenu(ColourMenu.class, new ColourMenu());
	}
}
