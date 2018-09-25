package deimophobe.nightfall.common.player.cosmetic;

import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 25/12/17.
 */
public class HatMenu extends ListMenu<Cosmetics> implements MainMenu<Cosmetics> {
	public HatMenu() {
		ConfigurationSection config = NightfallCommonPlugin.getInternalFileConfig("hats.yml");
		for (String key : config.getKeys(false)) {
			try {
				HatItem hatItem = HatItem.fromConfig(config.getConfigurationSection(key));
				addItem(hatItem);
			} catch (MalformedConfigurationException e) {
				NightfallCommonPlugin.logger().severe("Failed to create hat '" + key + "'");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getTitle() {
		return "Select a Hat";
	}
	
	@Override
	public Cosmetics getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getCosmetics(player);
	}
	
	@Override
	public String getMenuPermission() {
		return "hat";
	}
}