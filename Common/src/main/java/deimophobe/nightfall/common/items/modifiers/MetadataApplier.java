package deimophobe.nightfall.common.items.modifiers;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.inventory.meta.tags.ItemTagType;

/**
 * Created by Deimophobe on 24/01/19.
 */
class MetadataApplier implements ModifierApplier {
	private final NamespacedKey key;
	
	MetadataApplier(NamespacedKey key) {
		this.key = key;
	}
	
	@Override
	public ItemStack applyToItem(ItemStack item, int value) {
		ItemMeta meta = item.getItemMeta();
		CustomItemTagContainer container = meta.getCustomTagContainer();
		container.setCustomTag(key, ItemTagType.INTEGER, value);
		item.setItemMeta(meta);
		
		return item;
	}
}
