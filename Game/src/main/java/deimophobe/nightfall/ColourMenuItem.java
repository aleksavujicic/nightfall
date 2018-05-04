package deimophobe.nightfall;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.NoData;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class ColourMenuItem extends SimpleItem<NoData> {
	private final DyeColor colour;
	
	public ColourMenuItem(ItemStack item, DyeColor colour) {
		super(item);
		this.colour = colour;
	}
	
	@Override
	public boolean onClick(MenuSession<NoData> session) {
		Player player = session.getPlayer();
		dyeForPlayer(player);
		return false;
	}
	
	void dyeForPlayer(Player player) {
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		Material type = heldItem.getType();
		if (type == Material.GLASS || type == Material.STAINED_GLASS) {
			changeItem(heldItem);
		}
	}
	
	private void changeItem(ItemStack item) {
		if (colour == null) {
			item.setType(Material.GLASS);
		} else {
			item.setType(Material.STAINED_GLASS);
			item.setDurability(colour.getWoolData());
		}
	}
}
