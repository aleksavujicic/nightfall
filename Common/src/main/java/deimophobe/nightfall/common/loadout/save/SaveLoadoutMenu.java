package deimophobe.nightfall.common.loadout.save;

import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.menu.MainMenu;
import deimophobe.nightfall.common.menu.submenu.SimpleMenu;
import deimophobe.nightfall.common.player.PlayerInfo;
import deimophobe.nightfall.common.player.PlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 2/06/18.
 */
public class SaveLoadoutMenu extends SimpleMenu<PlayerInfo> implements MainMenu<PlayerInfo> {
	public SaveLoadoutMenu() {
		super(4 * 9);
		
		CustomItem save   = getItem("save");
		CustomItem load   = getItem("load");
		CustomItem export = getItem("export");
		
		for (int i = 0; i < 3; i++) {
			setItem(12 + i, new SaveItem(save, i));
			setItem(21 + i, new LoadItem(load, i));
		}
		
		// Runs into the issue of long strings, need a better way of encoding loadouts
		//setItem(15, new ExportItem(export.createItemStack()));
	}
	
	private CustomItem getItem(String name) {
		ConfigurationSection itemConfig = NightfallCommonPlugin.getInternalFileConfig("loadout/save-menu.yml");
		return CustomItem.getItem(itemConfig.getConfigurationSection(name), LoreTemplate.BASIC);
	}
	
	@Override
	public String getTitle() {
		return "Save/Load Kit";
	}
	
	@Override
	public PlayerInfo getDataFromPlayer(Player player) {
		return PlayerManager.getManager().getPlayerInfo(player);
	}
	
	@Override
	public String getMenuPermission() {
		return "loadout";
	}
}
