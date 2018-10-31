package deimophobe.nightfall.common.loadout.item;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.base.BaseItemManager;
import deimophobe.nightfall.common.loadout.Loadout;
import deimophobe.nightfall.common.loadout.LoadoutConstructable;
import deimophobe.nightfall.common.menu.MenuSession;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 20/12/17.
 */
public class SimpleLoadoutItem extends LoadoutItem {
	private final String pieceName;
	
	public SimpleLoadoutItem(ConfigurationSection config) {
		super(config);
		this.pieceName = config.getString("name");
	}
	
	@Override
	public void modify(Loadout loadout, LoadoutConstructable construct) {
		tryAddPiece(construct, pieceName);
	}
}
