package deimophobe.nightfall.common;

import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.loadout.LoadoutManager;
import deimophobe.nightfall.common.menu.MenuManager;
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
	
	@Override
	public void onEnable() {
		plugin = this;
		
		LoreTemplate.registerTemplateFile("lore-templates.yml");
		MenuManager.initialiseMenuManager(this);
		LoadoutManager.getManager();
	}
	
	public static YamlConfiguration getInternalFileConfig(String name) {
		InputStream stream = getPlugin().getResource(name);
		if (stream == null) throw new IllegalArgumentException("Unknown config file: " + name);
		return YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
	}
}
