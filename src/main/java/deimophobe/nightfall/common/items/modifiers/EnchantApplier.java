package deimophobe.nightfall.common.items.modifiers;

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
	public ItemStack applyToItem(ItemStack item, int value) {
		item.addUnsafeEnchantment(enchantType, value);
		return item;
	}
}
