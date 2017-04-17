package deimophobe.dvz.dwarf;

import deimophobe.dvz.items.ItemCreator;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.kit.consumable.Consumable;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.items.lore.LoreTemplate;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A static helper class for accessing dwarf items.
 * Created by Deimophobe on 31/03/17.
 */
public class DwarvenItems {
	
	private static final Configuration config;
	private static final Set<ItemStack> droppables = new HashSet<>();
	
	static {
		config = Misc.getInternalFileConfig("dwarf-items.yml");
		droppables.add(getItem("misc.pick"));
		droppables.add(getItem("misc.shovel"));
		droppables.add(getItem("misc.axe"));
	}
	
	
	public static ItemStack getItem(String sec, Slot slot) {
		return ItemCreator.createItem(config.getConfigurationSection(sec), slot);
	}
	
	public static ItemStack getItem(String section) {
		return  getItem(section, Slot.MAIN_HAND);
	}
	
	
	public static ItemStack getItem2(String sec) {
		return ItemCreator.createItem(config.getConfigurationSection(sec), LoreTemplate.DWARF, Collections.singletonMap("test", "var called test"));
	}
	
	public static boolean isDroppableItem(ItemStack item) {
		if (item == null) return true;
		
		for (ItemStack droppable : droppables)
			if (item.isSimilar(droppable))
				return true;
		
		for (ConsumableType consumableType : Consumable.droppableConsumables)
			if (item.isSimilar(Consumable.getItem(consumableType)))
				return true;
		
		return false;
	}
}
