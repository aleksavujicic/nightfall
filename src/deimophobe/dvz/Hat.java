package deimophobe.dvz;

import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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
	;
	
	private final ItemStack hat;
	private final String name;
	
	Hat(String hat) {
		name = hat;
		this.hat = ItemCreator.createItem(Misc.getInternalFileConfig("hats.yml").getConfigurationSection(hat), Slot.HEAD);
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
