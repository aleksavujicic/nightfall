package deimophobe.dvz.items.lore;

import deimophobe.dvz.Misc;
import deimophobe.dvz.items.modifiers.ItemModifier;
import deimophobe.dvz.items.modifiers.ItemModifierType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class LoreTemplate {
	
	public static final String BASIC = "basic";
	public static final String DWARF = "dwarf";
	public static final String DWARF_HERO = "dwarf-hero";
	public static final String LOADOUT = "loadout";
	public static final String MOB = "monster";
	public static final String MOB_HERO = "monster-hero";
	public static final String MOB_UPGRADE = "monster-upgrade";
	
	private final String namePrefix;
	private final List<LoreComponent> components;
	
	private final String modifierNamePrefix;
	private final String modifierValuePrefix;
	private final String modifierReasonPrefix;
	
	private LoreTemplate(ConfigurationSection config) {
		namePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("nameprefix"));
		
		components = new ArrayList<>();
		List<String> lore = config.getStringList("lore");
		String longLore = String.join("\n", lore);
		
		
		// Find sections
		int prevMatch = 0;
		sectionFinder: for (int i=0; i<longLore.length(); i++) {
			if (longLore.charAt(i) != '$') continue;
			if (i != 0 && longLore.charAt(i-1) == '\\') continue;
			if (longLore.charAt(i+1) != '{') continue;
			
			String prevString = longLore.substring(prevMatch, i);
			components.add(new StringComponent(prevString));
			
			int j = i+2;
			do {
				j++;
				if (j >= longLore.length()) break sectionFinder;
			} while (Character.isLetter(longLore.charAt(j)));
			
			String sectionName = longLore.substring(i+2, j);
			String secPrefix = config.getString("prefix."+sectionName, "&r");
			components.add(new SectionComponent(sectionName, secPrefix));
			
			prevMatch = j+1;
		}
		
		modifierNamePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.name"));
		modifierValuePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.value"));
		modifierReasonPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.reason"));
	}
	
	public String getName(String name) {
		return namePrefix + name;
	}
	
	public StringBuilder generateLoreText(Map<String, String> sections) {
		StringBuilder builder = new StringBuilder();
		for (LoreComponent component : components)
			builder.append(component.toString(sections));
		
		return builder;
	}
	
	public List<String> generateAttributeText(SortedMap<ItemModifierType, Set<ItemModifier>> modifiers) {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<ItemModifierType, Set<ItemModifier>> entry : modifiers.entrySet()) {
			ItemModifierType type = entry.getKey();
			Set<ItemModifier> modifierGroup = entry.getValue();
			
			// Get attribute name
			String name = type.getName();
			
			// Get the net value of attribute
			int total = 0;
			for (ItemModifier modifier : modifierGroup)
				total += modifier.getValue();
			
			if (total == 0) continue;
			
			// Format it
			String value = type.formatValue(total, false);
			
			// Add main line
			if (value == null)
				lines.add(modifierNamePrefix + name);
			else
				lines.add(modifierNamePrefix + name + ": " + modifierValuePrefix + value);
			
			// Add any reason lines
			for (ItemModifier modifier : modifierGroup) {
				String reason = modifier.getReason();
				if (reason == null) continue;
				
				String modValue = type.formatValue(modifier.getValue(), true);
				lines.add(modifierReasonPrefix + " " + modValue + " (" + reason + ")");
			}
		}
		
		return lines;
	}
	
	
	private static final HashMap<String, LoreTemplate> templates = new HashMap<>();
	static {
		YamlConfiguration templatesFile = Misc.getInternalFileConfig("lore-templates.yml");
		for (String key : templatesFile.getKeys(false)) {
			templates.put(key, new LoreTemplate(templatesFile.getConfigurationSection(key)));
		}
	}
	public static LoreTemplate getLoreTemplate(String name) {
		return templates.get(name);
	}
}
