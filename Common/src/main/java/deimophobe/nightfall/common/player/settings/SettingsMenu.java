package deimophobe.nightfall.common.player.settings;

import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.item.MenuItem;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Created by Deimophobe on 16/05/18.
 */
public class SettingsMenu extends SimpleMenu<PlayerSettings> implements MainMenu<PlayerSettings> {
	public static SettingsMenu createMenu(NightfallCommonPlugin plugin) {
		
		ConfigurationSection settingsConfig = plugin.readInternalFileConfig("settings.yml");
		SettingsMenuConfig config;
		try {
			config = new SettingsMenuConfig(settingsConfig);
		} catch (MalformedConfigurationException e) {
			e.printStackTrace();
			config = new SettingsMenuConfig();
		}
		return new SettingsMenu(config);
	}
	
	private SettingsMenu(SettingsMenuConfig config) {
		super(config.getSize());
		
		for (SettingsMenuConfig.ItemConfig itemConfig : config.getItemConfigs()) {
			int index = itemConfig.getIndex();
			SettingMenuItem item = new SettingMenuItem(itemConfig);
			
			this.setItem(index, item);
		}
	}
	
	@Override
	public String getTitle() {
		return "Settings";
	}
	
	@Override
	public PlayerSettings getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getSettings(player);
	}
	
	@Override
	public String getPermissionName() {
		return "settings";
	}
}
