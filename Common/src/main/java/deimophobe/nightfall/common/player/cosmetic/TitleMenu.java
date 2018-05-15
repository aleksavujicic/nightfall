package deimophobe.nightfall.common.player.cosmetic;

import deimophobe.nightfall.common.MalformedConfigurationException;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 23/12/17.
 */
public class TitleMenu extends ListMenu<Cosmetics> implements MainMenu<Cosmetics> {
	public TitleMenu() {
		ConfigurationSection config = NightfallCommonPlugin.getInternalFileConfig("titles.yml");
		for (String key : config.getKeys(false)) {
			try {
				addItem(new TitleItem(config.getConfigurationSection(key)));
			} catch (MalformedConfigurationException e) {
				NightfallCommonPlugin.logger().severe("Failed to create title '" + key + "'");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getTitle() {
		return "Select a Title";
	}
	
	@Override
	public Cosmetics getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getCosmetics(player);
	}
}
