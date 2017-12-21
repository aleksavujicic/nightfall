package deimophobe.nightfall.common;

import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deimophobe on 11/03/17.
 */
public class Hat {
	private final ItemStack hat;
	private final String name;
	
	private Hat(ConfigurationSection config) {
		name = config.getName().toLowerCase();
		this.hat = CustomItem.getItem(config, LoreTemplate.BASIC, Slot.HEAD).createItemStack();
	}
	
	public void putOn(Player player) {
		player.getInventory().setHelmet(hat);
	}
	
	public ItemStack asItemStack() {
		return hat;
	}
	
	
	
	private static final Map<String, Hat> HATS = new HashMap<>();
	static {
		ConfigurationSection config = Misc.getInternalFileConfig("common/hats.yml");
		for (String key : config.getKeys(false)) {
			Hat hat = new Hat(config.getConfigurationSection(key));
			HATS.put(hat.name, hat);
		}
	}
	
	public static Hat getHat(String name) {
		return HATS.get(name.toLowerCase());
	}
}
