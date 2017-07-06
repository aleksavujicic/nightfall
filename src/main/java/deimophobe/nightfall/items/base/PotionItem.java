package deimophobe.nightfall.items.base;

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
		if (color == null) throw new NullPointerException("Color for potion base cannot be null.");
		
		this.color = color;
	}
	
	@Override
	public ItemStack createItem() {
		ItemStack potion = new ItemStack(Material.POTION, 1);
		
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setColor(color);
		potion.setItemMeta(meta);
		
		return potion;
	}
	
	@Override
	public boolean isSimilar(BaseItem item) {
		if (item instanceof PotionItem) {
			return color.equals(((PotionItem) item).color);
		} else {
			return isSimilar(item.createItem());
		}
	}
	
	@Override
	public boolean isSimilar(ItemStack item) {
		if (item.getType() != Material.POTION) return false;
		
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		return (meta.getColor().equals(color));
	}
	
	@Override
	public PotionItem clone() {
		return new PotionItem(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
	}
}
