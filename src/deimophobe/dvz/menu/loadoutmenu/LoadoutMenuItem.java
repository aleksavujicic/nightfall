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
	
	private final ItemStack itemStack;
	private final LoadoutItem loadoutItem;
	
	LoadoutMenuItem(ConfigurationSection config) {
		loadoutItem = LoadoutItem.getItem(config.getName());
		itemStack = ItemCreator.createItem(config.getConfigurationSection("item"), Slot.MAIN_HAND);
		itemStack.setAmount(loadoutItem.getCost());
	}
	
	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack newItem = itemStack.clone();
		
		if (playerHasUpgrade(player))
			newItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		return newItem;
	}
	
	@Override
	public boolean select(Player player) {
		Loadout.getLoadout(player).selectItem(loadoutItem);
		return true;
	}
	
	@Override
	public boolean isAvailable(Player player) {
		return true;
	}
	
	private boolean playerHasUpgrade(Player player) {
		return Loadout.getLoadout(player).hasItem(loadoutItem);
	}
}
