package deimophobe.dvz.items;

import com.google.common.collect.ImmutableList;
import deimophobe.dvz.items.base.BaseItem;
import deimophobe.dvz.items.base.SimpleBaseItem;
import deimophobe.dvz.items.base.BaseItemManager;
import deimophobe.dvz.items.lore.Lore;
import deimophobe.dvz.items.lore.LoreTemplate;
import deimophobe.dvz.items.modifiers.ItemModifier;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.Slot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class CustomItem implements Cloneable {
	
	private final Slot slot;
	private final BaseItem base;
	private final Lore lore;
	private final SortedMap<ItemModifierType, Set<ItemModifier>> modifiers;
	
	public CustomItem(BaseItem base, Lore lore, SortedMap<ItemModifierType, Set<ItemModifier>> modifiers, Slot slot) {
		this.slot = slot;
		this.base = base.clone();
		this.lore = lore.clone();
		
		this.modifiers = new TreeMap<>();
		for (ItemModifierType type : modifiers.keySet()) {
			modifiers.put(type, new HashSet<>(modifiers.get(type)));
		}
	}
	
	private CustomItem(BaseItem base, LoreTemplate loreTemplate, String name, Map<String, String> loreSections, List<String> errors, Slot slot) {
		this.slot = slot;
		this.base = base;
		this.lore = new Lore(loreTemplate, name, loreSections, errors);
		
		this.modifiers = new TreeMap<>();
	}
	
	public void applyVariables(Map<String, String> variables) {
		lore.interpolateVariables(variables);
	}
	
	public void addModifier(ItemModifierType type, int value) {
		addModifier(type, value, null);
	}
	
	public void addModifier(ItemModifierType type, int value, String reason) {
		if (value == 0) return;
		
		modifiers.putIfAbsent(type, new HashSet<>());
		Set<ItemModifier> modifierGroup = modifiers.get(type);
		modifierGroup.add(new ItemModifier(value, reason));
	}
	
	public ItemStack createItem() {
		
		// Create item
		ItemStack item = base.createItem();
		ItemMeta meta = item.getItemMeta();
		
		// Set unbreakable and give item flags.
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
		
		// Add lore/name
		meta.setDisplayName(lore.createName());
		meta.setLore(lore.createLore(modifiers));
		item.setItemMeta(meta);
		
		// Add modifiers
		for (Map.Entry<ItemModifierType, Set<ItemModifier>> entry : modifiers.entrySet()) {
			ItemModifierType type = entry.getKey();
			int value = 0;
			for (ItemModifier modifier : entry.getValue()) {
				value += modifier.getValue();
			}
			item = type.applyModifier(item, value, slot);
		}
		
		return item;
	}
	
	public boolean isSimilar(BaseItem item) {
		return base.isSimilar(item);
	}
	
	public boolean isSimilar(ItemStack item) {
		return base.isSimilar(item);
	}
	
	@Override
	public CustomItem clone() {
		return new CustomItem(base, lore, modifiers, slot);
	}
	
	
	// ------ STATIC FACTORY METHODS ------
	public static CustomItem createItem(ConfigurationSection itemConfig, String baseTemplate, Slot slot) {
		return createItem(itemConfig, LoreTemplate.getLoreTemplate(baseTemplate), slot);
	}
	
	public static CustomItem createItem(ConfigurationSection itemConfig, LoreTemplate baseTemplate, Slot slot) {
		if (baseTemplate == null)
			throw new NullPointerException("Base template for item '" + itemConfig.getCurrentPath()  + "' must not be null.");
		
		List<String> errors = new ArrayList<>();
		
		// Get base item
		BaseItem baseItem;
		try {
			baseItem = getBaseItemFromConfig(itemConfig);
		} catch (InvalidConfigurationException e) {
			// Print error
			e.printStackTrace();
			
			// Create 'error' item with error in lore text
			baseItem = BaseItemManager.getErrorItem();
			errors.add(e.getMessage());
		}
		
		// Get template
		LoreTemplate loreTemplate;
		if (itemConfig.contains("template")) {
			String templateName = itemConfig.getString("template");
			loreTemplate = LoreTemplate.getLoreTemplate(templateName);
			
			
			if (loreTemplate == null) {
				Bukkit.getLogger().severe("Invalid template in config '" + templateName + "' for item '" + itemConfig.getCurrentPath() + "'. Defaulting to base template.");
				errors.add("Invalid template in config '" + templateName + "'");
				loreTemplate = baseTemplate;
			}
		} else {
			loreTemplate = baseTemplate;
		}
		
		// Get name
		String name = itemConfig.getString("name", "Default name");
		if (!itemConfig.contains("name"))
			errors.add("Name not specified");
		
		// Add lore sections if they exist
		Map<String, String> loreSections = new HashMap<>();
		if (itemConfig.contains("lore")) {
			ConfigurationSection sectionConfig = itemConfig.getConfigurationSection("lore");
			for (String key : sectionConfig.getKeys(false)) {
				loreSections.put(key, sectionConfig.getString(key));
			}
		}
		Collections.unmodifiableList(errors);
		
		// Create item
		CustomItem item = new CustomItem(baseItem, loreTemplate, name, loreSections, Collections.unmodifiableList(errors), slot);
		
		// Add modifiers if they exist
		if (itemConfig.contains("modifiers")) {
			ConfigurationSection modifierSection = itemConfig.getConfigurationSection("modifiers");
			for (String modifier : modifierSection.getKeys(false)) {
				try {
					ItemModifierType type = ItemModifierType.getByString(modifier);
					int value = modifierSection.getInt(modifier);
					
					item.addModifier(type, value);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					errors.add("Invalid modifier: " + modifier);
				}
			}
		}
		
		
		return item;
	}
	
	private static BaseItem getBaseItemFromConfig(ConfigurationSection itemConfig) throws InvalidConfigurationException {
		// Try find base node, and if not try find material node.
		if (itemConfig.contains("base")) {
			String name = itemConfig.getString("base");
			
			try {
				return BaseItemManager.getItem(name);
			} catch (IllegalArgumentException e) {
				throw new InvalidConfigurationException("Unknown base item '" + name + "' for item " + itemConfig.getCurrentPath());
			}
		} else if (itemConfig.contains("material")) {
			String name = itemConfig.getString("material");
			
			Material material = Material.matchMaterial(name);
			if (material == null) {
				throw new InvalidConfigurationException("Unknown material '" + name + "' for item " + itemConfig.getCurrentPath());
			}
			
			return new SimpleBaseItem(material);
		} else {
			throw new InvalidConfigurationException("No base or material section specified for item " + itemConfig.getCurrentPath());
		}
	}
}
