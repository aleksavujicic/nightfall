package deimophobe.nightfall.common.items;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.common.NightfallCommonPlugin;
import deimophobe.nightfall.common.UnknownEnumElementException;
import deimophobe.nightfall.common.items.base.BaseItem;
import deimophobe.nightfall.common.items.base.BaseItemManager;
import deimophobe.nightfall.common.items.base.SimpleBaseItem;
import deimophobe.nightfall.common.items.lore.Lore;
import deimophobe.nightfall.common.items.lore.LoreTemplate;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import minecraft.spigot.community.michel_0.api.ItemAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class CustomItem implements Cloneable, ItemMatcher {
	
	private BaseItem base;
	private final Lore lore;
	private final List<String> errors;
	private final SortedMap<ItemModifierType, Map<String, Integer>> modifiers;
	
	private final boolean bound;
	private boolean shiny;
	
	public void setShiny(boolean shiny) {
		this.shiny = shiny;
	}
	
	public CustomItem(BaseItem base, Lore lore, List<String> errors, SortedMap<ItemModifierType, Map<String, Integer>> modifiers, boolean bound, boolean shiny) {
		this.base = base;
		this.lore = lore.clone();
		this.errors = new ArrayList<>(errors);
		
		this.bound = bound;
		this.shiny = shiny;
		
		this.modifiers = new TreeMap<>();
		for (Map.Entry<ItemModifierType, Map<String, Integer>> entry : modifiers.entrySet()) {
			ItemModifierType type = entry.getKey();
			Map<String, Integer> newReasonMap = new HashMap<>(entry.getValue());
			this.modifiers.put(type, newReasonMap);
		}
	}
	
	private CustomItem(BaseItem base, LoreTemplate loreTemplate, String name, Map<String, String> loreSections, List<String> errors, boolean bound, boolean shiny) {
		this.base = base;
		this.lore = new Lore(loreTemplate, name, loreSections);
		this.errors = new ArrayList<>(errors);
		
		this.bound = bound;
		this.shiny = shiny;
		
		this.modifiers = new TreeMap<>();
	}
	
	public void setName(String name) {
		lore.setName(name);
	}
	
	public void applyVariable(String name, String value) {
		lore.applyVariable(name, value);
	}
	
	public void addModifier(ItemModifierType type, int value) {
		addModifier(type, value, null);
	}
	
	public void addModifier(ItemModifierType type, int value, String reason) {
		forceAddModifier(type, value, reason);
	}
	
	public void removeAllModifiers() {
		modifiers.clear();
	}
	
	public void removeModifier(ItemModifierType type) {
		modifiers.remove(type);
	}
	
	private void forceAddModifier(ItemModifierType type, int value, String reason) {
		if (value == 0) return;
		
		modifiers.putIfAbsent(type, new HashMap<>());
		Map<String, Integer> modifierGroup = modifiers.get(type);
		modifierGroup.putIfAbsent(reason, 0);
		modifierGroup.compute(reason, (k,v) -> v + value);
	}
	
	public void setBase(BaseItem item) {
		this.base = item;
	}
	
	public void addError(String error) {
		errors.add(error);
	}
	
	public ItemStack createItemStack() {
		//Bukkit.getLogger().info("Creating item: "+ lore.createName());
		
		// Create item
		ItemStack item = base.createItem();
		ItemMeta meta = item.getItemMeta();
		
		// Set unbreakable and give item flags.
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS);
				
		// Add lore/name
		meta.setDisplayName(lore.createName());
		meta.setLore(lore.createLore(modifiers, errors));
		item.setItemMeta(meta);
		
		// Remove existing attributes (mainly for armour)
		item = new ItemAttributes().apply(item);
		
		// Add modifiers
		for (Map.Entry<ItemModifierType, Map<String, Integer>> entry : modifiers.entrySet()) {
			ItemModifierType type = entry.getKey();
			int totalValue = 0;
			for (Integer value : entry.getValue().values()) {
				totalValue += value;
			}
			item = type.applyModifier(item, totalValue);
		}
		
		// Give bound and shiny
		if (bound) item.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
		if (shiny) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		
		
		return item;
	}
	
	public boolean isSimilar(CustomItem item) {
		if (item == null) return false;
		return (
			lore.createName().equals(item.lore.createName()) &&
			base.isSimilar(item.base)
		);
	}
	
	public boolean isSimilar(ItemStack item) {
		if (item == null) return false;
		if (item.getItemMeta() == null) return false;
		return (
				lore.createName().equals(item.getItemMeta().getDisplayName()) &&
				base.isSimilar(item)
		);
	}
	
	@Override
	public boolean doesItemMatch(@NotNull ItemStack item) {
		return isSimilar(item);
	}
	
	@Override
	public CustomItem clone() {
		return new CustomItem(base, lore, errors, modifiers, bound, shiny);
	}
	
	public CustomItem immutableCopy() {
		return new ImmutableCustomItem(base, lore, errors, modifiers, bound, shiny);
	}
	
	
	public static CustomItem tryClone(CustomItem item) {
		if (item == null) return null;
		return item.clone();
	}
	
	// ------ STATIC FACTORY METHODS ------
	public static CustomItem getItem(ConfigurationSection itemConfig, String baseTemplate) {
		return getItem(itemConfig, LoreTemplate.getLoreTemplate(baseTemplate));
	}
	
	public static CustomItem getItem(ConfigurationSection itemConfig, LoreTemplate baseTemplate) {
		if (itemConfig == null)
			throw new NullPointerException("Item config must not be null.");
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
			baseItem = BaseItemManager.getManager().getErrorItem();
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
			if (sectionConfig != null) {
				for (String key : sectionConfig.getKeys(false)) {
					List<String> possibleLoreSec = sectionConfig.getStringList(key);
					if (!possibleLoreSec.isEmpty()) {
						loreSections.put(key, Misc.getRandom(possibleLoreSec));
					} else {
						String loreSection = sectionConfig.getString(key);
						loreSections.put(key, loreSection);
					}
				}
			}
		}
		
		// Other properties
		boolean bound = itemConfig.getBoolean("bound", false);
		boolean shiny = itemConfig.getBoolean("shiny", false);
		
		// Create item
		CustomItem item = new CustomItem(baseItem, loreTemplate, name, loreSections, Collections.unmodifiableList(errors), bound, shiny);
		
		// Add modifiers if they exist
		if (itemConfig.contains("modifiers")) {
			ConfigurationSection modifierSection = itemConfig.getConfigurationSection("modifiers");
			for (String modifier : modifierSection.getKeys(false)) {
				try {
					ItemModifierType type = ItemModifierType.getByString(modifier);
					int value = modifierSection.getInt(modifier);
					
					item.addModifier(type, value);
				} catch (UnknownEnumElementException e) {
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
			
			if (name.equalsIgnoreCase("temp") || name.equalsIgnoreCase("temporary")) {
				NightfallCommonPlugin.getPlugin().getLogger().warning("Accessing temp item in item: " + itemConfig.getCurrentPath());
				return BaseItemManager.getManager().getTempItem();
			}
			
			BaseItem item = BaseItemManager.getManager().getItem(name);
			if (item == null) throw new InvalidConfigurationException("Unknown base item '" + name + "' for item: " + itemConfig.getCurrentPath());
			return item;
			
		} else if (itemConfig.contains("material")) {
			String name = itemConfig.getString("material");
			
			Material material = Material.matchMaterial(name);
			if (material == null) {
				throw new InvalidConfigurationException("Unknown material '" + name + "' for item: " + itemConfig.getCurrentPath());
			}
			
			return new SimpleBaseItem(material);
		} else {
			throw new InvalidConfigurationException("No base or material section specified for item: " + itemConfig.getCurrentPath());
		}
	}
}
