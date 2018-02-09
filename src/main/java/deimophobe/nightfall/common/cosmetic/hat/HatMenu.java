package deimophobe.nightfall.common.cosmetic.hat;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.cosmetic.Cosmetics;
import deimophobe.nightfall.common.cosmetic.CosmeticManager;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 25/12/17.
 */
public class HatMenu extends ListMenu<Cosmetics> implements MainMenu<Cosmetics> {
	public HatMenu() {
		ConfigurationSection config = NightfallCommonPlugin.getInternalFileConfig("hat-menu.yml");
		for (String key : config.getKeys(false)) {
			try {
				addItem(new HatItem(config.getConfigurationSection(key)));
			} catch (InvalidConfigurationException e) {
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
		return CosmeticManager.getManager().getCosmetic(player);
	}
}