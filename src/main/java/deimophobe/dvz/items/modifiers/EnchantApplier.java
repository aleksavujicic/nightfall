package deimophobe.dvz.items.modifiers;

import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 27/04/17.
 */
class EnchantApplier implements ModifierApplier {
	private final Enchantment enchantType;
	
	EnchantApplier(Enchantment enchantType) {
		this.enchantType = enchantType;
	}
	
	@Override
	public ItemStack applyToItem(ItemStack item, int value, Slot slot) {
		item.addUnsafeEnchantment(enchantType, value);
		return item;
	}
}
