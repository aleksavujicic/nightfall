package deimophobe.nightfall.common;

import deimophobe.nightfall.common.command.CommonCommandInitialiser;
import deimophobe.nightfall.common.database.DataIO;
import deimophobe.nightfall.common.database.DataIOType;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.menu.MenuManager;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Created by Deimophobe on 7/01/18.
 */
public class NightfallCommonPlugin extends JavaPlugin {
	private static NightfallCommonPlugin plugin;
	public static NightfallCommonPlugin getPlugin() { return plugin; }
	
	public static Logger logger() { return plugin.getLogger(); }
	
	private DataIO dataIO;
	public DataIO getDataIO() { return dataIO; }
	
	private PlayerManager playerManager;
	public PlayerManager getPlayerManager() { return playerManager; }
	
	private FileConfiguration config;
	
	@Override
	public void onEnable() {
		plugin = this;
		LoreTemplate.registerTemplateFile("lore-templates.yml");
		
		CommonCommandInitialiser.initialiseCommands(this);
		
		// Load config - saving default if none exists.
		this.saveDefaultConfig();
		config = this.getConfig();
		
		DataIOType type = getDataIOType();
		dataIO = type.createDataIO(this);
		
		playerManager = new PlayerManager(this);
		
		MenuManager.initialiseMenuManager(this);
		LoadoutManager.getManager();
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		playerManager.saveAll();
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
	
	private DataIOType getDataIOType() {
		String databaseType = config.getString("database.type", "none");
		Logger logger = NightfallCommonPlugin.logger();
		try {
			DataIOType type = Misc.getEnumMemberFromString(databaseType, DataIOType.values(), "data handler");
			logger.info("Using database type: " + type);
			return type;
		} catch (UnknownEnumElementException e) {
			logger.warning("Unknown database: " + databaseType + ". Defaulting to NONE.");
			return DataIOType.NONE;
		}
	}
}
