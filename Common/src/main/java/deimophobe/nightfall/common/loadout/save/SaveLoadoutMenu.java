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
		CustomItem encode = getItem("encode");
		
		setItem(10, new SaveItem(save, 0));
		setItem(11, new SaveItem(save, 1));
		setItem(12, new SaveItem(save, 2));
		setItem(19, new LoadItem(load, 0));
		setItem(20, new LoadItem(load, 1));
		setItem(21, new LoadItem(load, 2));
		
		setItem(15, new EncodeItem(encode.createItemStack()));
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
}
