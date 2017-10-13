package deimophobe.nightfall.dwarf.loadout;

import deimophobe.nightfall.menu.SimpleMenu;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Created by Deimophobe on 2/03/17.
 */
class LoadoutPage extends SimpleMenu<Loadout> {
	public LoadoutPage(ConfigurationSection config) {
		super(LoadoutMenu.PAGE_SIZE);
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection itemConfig = config.getConfigurationSection(key);
			LoadoutItem item = new LoadoutItem(itemConfig);
			if (item.isEnabled())
				setItem(itemConfig.getInt("index"), item);
		}
	}
}
