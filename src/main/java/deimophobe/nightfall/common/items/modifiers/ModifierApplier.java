package deimophobe.nightfall.common.items.modifiers;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
interface ModifierApplier {
	ItemStack applyToItem(ItemStack item, int value);
	
	ModifierApplier DUD_APPLIER = (item, value) -> item;
}
