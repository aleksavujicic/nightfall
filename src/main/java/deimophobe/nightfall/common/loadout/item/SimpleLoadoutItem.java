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
	public void modify(LoadoutConstructable construct) {
		tryAddPiece(construct, pieceName);
	}
	
	
	@Override
	public ItemStack getDisplayItem(MenuSession<Loadout> session) {
		
		// TODO This is a hack to test a proof of concept for better item selection
		if (pieceName.equals("untimely_demise")) {
			
			Loadout loadout = session.getData();
			if (loadout.hasItem(this)) {
				CustomItem item = getItem().clone();
				item.setBase(BaseItemManager.getManager().getItem("doom-clock.on"));
				return item.createItemStack();
			} else {
				return super.getDisplayItem(session);
			}
		} else {
			return super.getDisplayItem(session);
		}
	}
}
