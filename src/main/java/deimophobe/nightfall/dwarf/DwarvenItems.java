package deimophobe.nightfall.dwarf;

import deimophobe.nightfall.NightfallPlugin;
import deimophobe.nightfall.common.items.CustomItem;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.dwarf.consumable.ConsumableType;
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
	
	private static final Map<String, Configuration> configs = new HashMap<>();
	private static final Set<ItemStack> droppables = new HashSet<>();
	
	
	static {
		addConfigSection("melee","melee");
		addConfigSection("ranged","ranged");
		addConfigSection("healing","healing");
		addConfigSection("accessory","accessory");
		
		addConfigSection("consumable","consumable");
		addConfigSection("armour","dwarven-armour");
		addConfigSection("misc","misc");
		
		addConfigSection("hero","hero");
		addConfigSection("hero-hat","hero-hat");
		
		
		droppables.add(getItem("misc","pick").createItemStack());
		droppables.add(getItem("misc","shovel").createItemStack());
		droppables.add(getItem("misc","axe").createItemStack());
	}
	private static void addConfigSection(String section, String filename) {
		configs.put(section, NightfallPlugin.getInternalFileConfig("dwarf-items/"+ filename+".yml"));
	}
	
	
	
	public static CustomItem getItem(String section, String name) {
		return getItem(section, name, Slot.MAIN_HAND);
	}
	
	public static CustomItem getItem(String section, String name, Slot slot) {
		if (!configs.containsKey(section))
			throw new IllegalArgumentException("Dwarf item section '" + section + "' does not exist.");
		
		Configuration config = configs.get(section);
		if (!config.contains(name))
			throw new IllegalArgumentException("Dwarf item '" + name +  "' does not exist in section '" + section + "'.");
		
		LoreTemplate template = LoreTemplate.getLoreTemplate(LoreTemplate.DWARF);
		switch (section) {
			case "hero":
			case "hero-hat":
				template = LoreTemplate.getLoreTemplate(LoreTemplate.DWARF_HERO);
		}
		return CustomItem.getItem(config.getConfigurationSection(name), template, slot);
	}
	
	
	
	public static boolean isDroppableItem(ItemStack item) {
		if (item == null) return true;
		if (item.getType() == Material.AIR) return true;
		
		for (ItemStack droppable : droppables)
			if (item.isSimilar(droppable))
				return true;
		
		return ConsumableType.isDroppable(item);
		
	}
	
	public static Map<String,CustomItem> getAllItems() {
		Map<String, CustomItem> items = new HashMap<>();
		for (String section : configs.keySet()) {
			Configuration config = configs.get(section);
			for (String key : config.getKeys(true))
				if (config.contains(key + ".name")) // Kinda a hack to check if it is an item.
					items.put(section + "." + key, getItem(section, key));
		}
		return items;
	}
}
