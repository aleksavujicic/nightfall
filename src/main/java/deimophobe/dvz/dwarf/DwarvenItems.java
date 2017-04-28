package deimophobe.dvz.dwarf;

import deimophobe.dvz.items.CustomItem;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.kit.consumable.Consumable;
import deimophobe.dvz.dwarf.kit.consumable.ConsumableType;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

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
		droppables.add(createItemStack("misc.pick"));
		droppables.add(createItemStack("misc.shovel"));
		droppables.add(createItemStack("misc.axe"));
	}
	
	
	
	public static CustomItem getItem(String section) {
		return getItem(section, Slot.MAIN_HAND);
	}
	
	public static CustomItem getItem(String section, Slot slot) {
		return CustomItem.getItem(config.getConfigurationSection(section), LoreTemplate.DWARF, slot);
	}
	
	
	public static ItemStack createItemStack(String section) {
		return createItemStack(section, Slot.MAIN_HAND);
	}
	
	public static ItemStack createItemStack(String sec, Slot slot) {
		return getItem(sec, slot).createItemStack();
	}
	
	
	public static CustomItem getBow(String bow, int power) {
		CustomItem item = getItem("bow."+bow, Slot.MAIN_HAND);
		item.addModifier(ItemModifierType.POWER, power);
		return item;
	}
	
	public static boolean isDroppableItem(ItemStack item) {
		if (item == null) return true;
		
		for (ItemStack droppable : droppables)
			if (item.isSimilar(droppable))
				return true;
		
		for (ConsumableType consumableType : Consumable.droppableConsumables)
			if (item.isSimilar(Consumable.getItemStack(consumableType)))
				return true;
		
		return false;
	}
}
