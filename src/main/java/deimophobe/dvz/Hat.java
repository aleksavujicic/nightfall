package deimophobe.dvz;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.items.lore.LoreTemplate;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 11/03/17.
 */
public enum Hat {
	CROWN("crown"),
	FLOWER("flowers"),
	WITCH("witch"),
	DVGOGGLES("darkvisgoggles"),
	JIMMYCAP("jimmycap"),
	
	TUI("tuihat"),
	NOSOVIN("nosovinhat"),
	ARTHEA("arthea-hat"),
	;
	
	private final ItemStack hat;
	private final String name;
	
	Hat(String hat) {
		name = hat;
		ConfigurationSection config = Misc.getInternalFileConfig("hats.yml").getConfigurationSection(hat);
		this.hat = CustomItem.getItem(config, LoreTemplate.BASIC, Slot.HEAD).createItemStack();
	}
	
	public void putOn(GamePlayer player) {
		player.getPlayer().getInventory().setHelmet(hat);
	}
	
	public static Hat getHat(String name) {
		for (Hat hat : values()) {
			if (hat.name.equalsIgnoreCase(name))
				return hat;
		}
		return null;
	}
}
