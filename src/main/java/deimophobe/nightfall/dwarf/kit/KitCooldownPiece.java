package deimophobe.nightfall.dwarf.kit;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 19/03/17.
 */
public interface KitCooldownPiece extends KitPiece {
	float fractionComplete();
	ItemStack getCooldownToggleItem();
}
