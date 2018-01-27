package deimophobe.nightfall.common.items.modifiers;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
class DudApplier implements ModifierApplier {
	@Override
	public ItemStack applyToItem(ItemStack item, int value) {
		return item;
	}
}
