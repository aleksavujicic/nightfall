package deimophobe.dvz.menu.loadoutmenu;

import deimophobe.dvz.menu.SinglePageMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 2/03/17.
 */
class LoadoutPage extends SinglePageMenu<Player> {
	public LoadoutPage(ConfigurationSection config) {
		super("", LoadoutMenu.PAGE_SIZE/9);
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection itemConfig = config.getConfigurationSection(key);
			addItem(itemConfig.getInt("index"), new LoadoutMenuItem(itemConfig));
		}
	}
	
	
	@Override
	public void showTo(Player player) {
		player.openInventory(getInventory(player));
	}
}
