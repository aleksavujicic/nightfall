package deimophobe.nightfall;

import deimophobe.nightfall.command.CommandInitialiserUtil;
import deimophobe.nightfall.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Deimophobe on 15/01/17.
 */
public class NightfallPlugin extends JavaPlugin {
	
	private static NightfallPlugin plugin;
	private GameListener gl;
	
	public static NightfallPlugin getPlugin() {return plugin;}
	
	private boolean disabling = false;
	public boolean isDisabling() { return disabling; }
	
	@Override
	public void onEnable() {
		plugin = this;
		
		PacketUtil.setupListeners();
		
		gl = new GameListener();
		Game.createNewGame();
		Bukkit.getPluginManager().registerEvents(gl, NightfallPlugin.getPlugin());
		
		CommandInitialiserUtil.initialiseCommands(this);
	}

	@Override
	public void onDisable() {
		disabling = true;
		Game.getGame().stop();
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
}
