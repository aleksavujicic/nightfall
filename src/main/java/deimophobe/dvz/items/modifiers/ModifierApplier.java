package deimophobe.dvz.items.modifiers;

import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
interface ModifierApplier {
	ItemStack applyToItem(ItemStack item, int value, Slot slot);
}
