package deimophobe.dvz.menu.loadoutmenu;

import com.comphenix.protocol.PacketType;
import deimophobe.dvz.ItemCreator;
import deimophobe.dvz.dwarf.kit.ale.AleType;
import deimophobe.dvz.dwarf.kit.sword.SwordType;
import deimophobe.dvz.menu.MenuItem;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Deimophobe on 6/03/17.
 */
class LoadoutMenuItem implements MenuItem<Player> {
	
	private final ItemStack item;
	private final LoadoutItem loadoutItem;
	private final int cost;
	
	LoadoutMenuItem(ConfigurationSection config) {
		item = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
		cost = config.getInt("cost");
		loadoutItem = new LoadoutItem(config.getString("value"));
	}
	
	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack newItem = item.clone();
		
		if (playerHasUpgrade(player))
			newItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		//if (playerHasSimilarUpgrade(player))
		//	newItem.setAmount(-1);
		
		return newItem;
	}
	
	@Override
	public boolean select(Player player) {
		Bukkit.broadcastMessage(loadoutItem.getType().toString());
		Bukkit.broadcastMessage(loadoutItem.getValue().toString());
		return false;
	}
	
	@Override
	public boolean isAvailable(Player player) {
		return true;
	}
	
	private boolean playerHasUpgrade(Player player) {
		return false;
	}
	
	private boolean playerHasSimilarUpgrade(Player player) {
		return true;
	}
}
