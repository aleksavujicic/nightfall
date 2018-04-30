package deimophobe.nightfall.common;

import co.aikar.commands.BukkitCommandManager;
import deimophobe.nightfall.common.command.HatCommand;
import deimophobe.nightfall.common.command.LoadoutCommand;
import deimophobe.nightfall.common.command.TitleCommand;
import deimophobe.nightfall.common.database.DataHandler;
import deimophobe.nightfall.common.database.DataHandlerType;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.menu.MenuManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Deimophobe on 7/01/18.
 */
public class NightfallCommonPlugin extends JavaPlugin {
	private static NightfallCommonPlugin plugin;
	public static NightfallCommonPlugin getPlugin() { return plugin; }
	
	private DataHandler dataHandler;
	public static DataHandler getDataHandler() { return plugin.dataHandler; }
	
	private FileConfiguration config;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		// Load config - saving default if none exists.
		this.saveDefaultConfig();
		config = this.getConfig();
		
		dataHandler = createDataHandler();
		
		LoreTemplate.registerTemplateFile("lore-templates.yml");
		MenuManager.initialiseMenuManager(this);
		LoadoutManager.getManager();
		
		BukkitCommandManager bcm = new BukkitCommandManager(this);
		bcm.registerCommand(new LoadoutCommand());
		bcm.registerCommand(new HatCommand());
		bcm.registerCommand(new TitleCommand());
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
	
	private DataHandler createDataHandler() {
		String databaseType = config.getString("database.type", "none");
		try {
			DataHandlerType type = Misc.getEnumMemberFromString(databaseType, DataHandlerType.values(), "data handler");
			getLogger().info("Using database type: " + type);
			return type.getDataHandler();
		} catch (UnknownEnumElementException e) {
			getLogger().severe("Unknown database: " + databaseType + ". Defaulting to NONE.");
			return DataHandlerType.NONE.getDataHandler();
		}
	}
}
