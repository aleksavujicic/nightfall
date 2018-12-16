package deimophobe.nightfall;

import deimophobe.nightfall.common.menu.MenuSession;
import deimophobe.nightfall.common.menu.NoData;
import deimophobe.nightfall.common.menu.item.SimpleItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 4/05/18.
 */
public class ColourMenuItem extends SimpleItem<NoData> {
	private final Material colour;
	
	public ColourMenuItem(ItemStack item, Material colour) {
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
		if (isGlass(type)) {
			item.setType(colour);
		}
	}
	
	private static boolean isGlass(Material material) {
		switch (material) {
				case GLASS:
				case WHITE_STAINED_GLASS:
				case ORANGE_STAINED_GLASS:
				case MAGENTA_STAINED_GLASS:
				case LIGHT_BLUE_STAINED_GLASS:
				case YELLOW_STAINED_GLASS:
				case LIME_STAINED_GLASS:
				case PINK_STAINED_GLASS:
				case GRAY_STAINED_GLASS:
				case LIGHT_GRAY_STAINED_GLASS:
				case CYAN_STAINED_GLASS:
				case PURPLE_STAINED_GLASS:
				case BLUE_STAINED_GLASS:
				case BROWN_STAINED_GLASS:
				case GREEN_STAINED_GLASS:
				case RED_STAINED_GLASS:
				case BLACK_STAINED_GLASS:
					return true;
				
				default:
					return false;
		}
	}
}
