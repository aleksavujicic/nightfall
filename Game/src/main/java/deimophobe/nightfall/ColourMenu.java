package deimophobe.nightfall;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.NoData;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class ColourMenu extends ListMenu<NoData> implements MainMenu<NoData> {
	@Override public String getTitle() { return "Choose a colour"; }
	@Override public NoData getDataFromPlayer(Player player) { return null; }
	
	private Map<DyeColor, ColourMenuItem> colourMap = new HashMap<>();
	
	ColourMenu() {
		final Logger logger = NightfallPlugin.logger();
		final ConfigurationSection config = NightfallPlugin.getInternalFileConfig("colour-menu.yml");
		for (String key : config.getKeys(false)){
			ConfigurationSection colourConfig = config.getConfigurationSection(key);
			
			DyeColor colour;
			try {
				String colourName = colourConfig.getString("colour");
				if (colourName.equals("none")) {
					colour = null;
				} else {
					colour = Misc.getEnumMemberFromString(colourName, DyeColor.values(), "colour");
				}
			} catch (UnknownEnumElementException e) {
				logger.severe("Failed to parse colour name");
				e.printStackTrace();
				continue;
			}
			
			CustomItem item = CustomItem.getItem(colourConfig.getConfigurationSection("item"), "colour-menu");
			ColourMenuItem menuItem = new ColourMenuItem(item.createItemStack(), colour);
			this.addItem(menuItem);
			colourMap.put(colour, menuItem);
		}
	}
	
	public void dyeColour(Player player, DyeColor colour) {
		ColourMenuItem item = colourMap.get(colour);
		item.dyeForPlayer(player);
	}
}
