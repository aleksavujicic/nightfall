package deimophobe.nightfall;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.NoData;
import deimophobe.nightfall.common.menu.submenu.ListMenu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class ColourMenu extends ListMenu<NoData> implements MainMenu<NoData> {
	@Override public String getTitle() { return "Choose a colour"; }
	@Override public NoData getDataFromPlayer(Player player) { return null; }
	@Override public String getPermissionName() { return "colour"; }
	
	private Map<Material, ColourMenuItem> colourMap = new EnumMap<>(Material.class);
	
	ColourMenu() {
		final Logger logger = NightfallPlugin.logger();
		final ConfigurationSection config = NightfallPlugin.getInternalFileConfig("colour-menu.yml");
		for (String key : config.getKeys(false)){
			ConfigurationSection colourConfig = config.getConfigurationSection(key);
			
			Material colour;
			try {
				String colourName = colourConfig.getString("colour");
				if (colourName.equals("none")) {
					colour = Material.GLASS;
				} else {
					colour = Misc.getEnumMemberFromString(colourName + "-stained-glass", Material.values(), "colour");
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
		Material material = fromDyeColour(colour);
		ColourMenuItem item = colourMap.get(material);
		item.dyeForPlayer(player);
	}
	
	private static Material fromDyeColour(DyeColor colour) {
		if (colour == null) return Material.GLASS;
		
		switch (colour) {
			case WHITE: return Material.WHITE_STAINED_GLASS;
			case ORANGE: return Material.ORANGE_STAINED_GLASS;
			case MAGENTA: return Material.MAGENTA_STAINED_GLASS;
			case LIGHT_BLUE: return Material.LIGHT_BLUE_STAINED_GLASS;
			case YELLOW: return Material.YELLOW_STAINED_GLASS;
			case LIME: return Material.LIME_STAINED_GLASS;
			case PINK: return Material.PINK_STAINED_GLASS;
			case GRAY: return Material.GRAY_STAINED_GLASS;
			case LIGHT_GRAY: return Material.LIGHT_GRAY_STAINED_GLASS;
			case CYAN: return Material.CYAN_STAINED_GLASS;
			case PURPLE: return Material.PURPLE_STAINED_GLASS;
			case BLUE: return Material.BLUE_STAINED_GLASS;
			case BROWN: return Material.BROWN_STAINED_GLASS;
			case GREEN: return Material.GREEN_STAINED_GLASS;
			case RED: return Material.RED_STAINED_GLASS;
			case BLACK: return Material.BLACK_STAINED_GLASS;
		}
		throw new RuntimeException("Unknown DyeColor: " + colour);
	}
}
