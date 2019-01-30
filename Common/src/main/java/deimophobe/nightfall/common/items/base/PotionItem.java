package deimophobe.nightfall.common.items.base;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.EnumSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 15/04/17.
 */
class PotionItem implements BaseItem {
	private static final Set<Material> VALID_MATERIALS = EnumSet.of(
		Material.POTION, Material.TIPPED_ARROW
	);
	
	private final Material material;
	private final Color color;
	
	PotionItem(Material material, Color color) {
		checkArgument(VALID_MATERIALS.contains(material), "Material must be a valid potion material (got %s)", material);
		
		this.material = material;
		this.color = color;
	}
	
	@Override
	public ItemStack createItem() {
		ItemStack potion = new ItemStack(material, 1);
		
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		meta.setColor(color);
		potion.setItemMeta(meta);
		
		return potion;
	}
	
	@Override
	public boolean isSimilar(BaseItem item) {
		if (item instanceof PotionItem) {
			return color.equals(((PotionItem) item).color);
		}
		return false;
	}
	
	@Override
	public boolean doesItemMatch(ItemStack item) {
		if (item.getType() != material) return false;
		
		item.getItemMeta();
		
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		return (meta.getColor().equals(color));
	}
	
	@Override
	public PotionItem clone() {
		return new PotionItem(material, Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
	}
}
