package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.Misc;
import deimophobe.nightfall.dwarf.consumable.Consumable;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
import deimophobe.nightfall.items.CustomItem;
import deimophobe.nightfall.items.lore.LoreTemplate;
import deimophobe.nightfall.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		if (item.getType() == Material.AIR) return true;
		
		for (ItemStack droppable : droppables)
			if (item.isSimilar(droppable))
				return true;
		
		for (ConsumableType consumableType : Consumable.droppableConsumables)
			if (item.isSimilar(Consumable.getItemStack(consumableType)))
				return true;
		
		return false;
	}
	
	public static Map<String,CustomItem> getAllItems() {
		Map<String, CustomItem> items = new HashMap<>();
		for (String key : config.getKeys(true)) {
			if (config.contains(key + ".name")) // Kinda a hack to check if it is an item.
				items.put(key, getItem(key));
		}
		return items;
	}
}
