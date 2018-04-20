package deimophobe.nightfall.common.cosmetic.hat;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class Hat {
	private final ItemStack hat;
	private final String name;
	
	public Hat(ConfigurationSection config) {
		name = config.getName().toLowerCase();
		this.hat = CustomItem.getItem(config, LoreTemplate.BASIC).createItemStack();
	}
	
	public void putOn(Player player) {
		player.getInventory().setHelmet(hat);
	}
	
	public ItemStack asItemStack() {
		return hat;
	}
}
