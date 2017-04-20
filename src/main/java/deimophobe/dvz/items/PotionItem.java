package deimophobe.dvz.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * Created by Deimophobe on 15/04/17.
 */
class PotionItem implements BaseItem {
	
	private final Color color;
	
	PotionItem(Color color) {
		this.color = color;
	}
	
	@Override
	public ItemStack createItem() {
		ItemStack potion = new ItemStack(Material.POTION, 1);
		
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setColor(color);
		
		return potion;
	}
}
