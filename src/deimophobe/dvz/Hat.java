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
	
	TUI("tuihat"),
	;
	
	private final ItemStack hat;
	
	Hat(String hat) {
		ConfigurationSection hatData = YamlConfiguration.loadConfiguration(Game.getGame().getPlugin().getResource("hats.yml"));
		this.hat = ItemCreator.createItem(hatData.getConfigurationSection(hat), Slot.HEAD);
	}
	
	public void putOn(GamePlayer player) {
		player.getPlayer().getInventory().setHelmet(hat);
	}
}
