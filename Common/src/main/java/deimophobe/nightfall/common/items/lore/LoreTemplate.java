package deimophobe.nightfall.common.items.lore;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class LoreTemplate {
	private static final Pattern SECTION_REGEX = Pattern.compile("^\\$\\{([a-zA-Z0-9\\-]+)\\}$");
	
	public static final String BASIC = "basic";
	public static final String DEFAULT = BASIC;
	
	public static final String LOBBY = "lobby";
	
	public static final String LOADOUT = "loadout";
	public static final String TITLE = "title";
	public static final String HAT = "hat";
	
	public static final String DWARF = "dwarf";
	public static final String DWARF_HERO = "dwarf-hero";
	
	public static final String MOB = "monster";
	public static final String MOB_HERO = "monster-hero";
	public static final String MOB_UPGRADE = "monster-upgrade";
	
	private final String namePrefix;
	private final List<SectionTemplate> sectionTemplates;
	
	private final String modifierNamePrefix;
	private final String modifierValuePrefix;
	private final String modifierReasonPrefix;
	
	private LoreTemplate(ConfigurationSection config) {
		this.namePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("nameprefix"));
		
		this.sectionTemplates = new ArrayList<>();
		List<String> lore = config.getStringList("lore");
		final List<String> currentFixedTemplate = new ArrayList<>();
		
		// Kinda a hack to run this code in multiple places
		final Runnable fixedTemplateSaver = () -> {
			if (currentFixedTemplate.isEmpty()) return;
			
			String fixedString = String.join("\n", currentFixedTemplate);
			SectionTemplate fixedTemplate = new FixedSectionTemplate(fixedString);
			sectionTemplates.add(fixedTemplate);
			currentFixedTemplate.clear();
		};
		
		for (String line : lore) {
			Matcher matcher = SECTION_REGEX.matcher(line);
			if (matcher.matches()) {
				// First finish current fixed template
				fixedTemplateSaver.run();
				
				// Then add named section
				String name = matcher.group(1);
				String sectionPrefix = config.getString("prefix."+name, "&r");
				String sectionDefault = config.getString("defaults."+name, "");
				SectionTemplate section = new NamedSectionTemplate(name, sectionPrefix, sectionDefault);
				sectionTemplates.add(section);
			} else {
				currentFixedTemplate.add(line);
			}
		}
		// Save any lines at the end
		fixedTemplateSaver.run();
		
		modifierNamePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.name", ""));
		modifierValuePrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.value", ""));
		modifierReasonPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("modifiers.reason", ""));
	}
	
	public String getName(String name) {
		return namePrefix + name;
	}
	
	List<Section> createSections(Map<String, String> loreSections) {
		List<Section> sectionList = new ArrayList<>();
		for (SectionTemplate sectionTemplate : sectionTemplates) {
			Section section = sectionTemplate.createSection(loreSections);
			sectionList.add(section);
		}
		
		return sectionList;
	}
	
	List<String> generateAttributeText(Map<ItemModifierType, Map<String, Integer>> modifiers) {
		List<String> lines = new ArrayList<>();
		for (Map.Entry<ItemModifierType, Map<String, Integer>> entry : modifiers.entrySet()) {
			ItemModifierType type = entry.getKey();
			Map<String, Integer> modifierGroup = entry.getValue();
			
			// Get attribute name
			String name = type.getName();
			
			// Get the net value of attribute
			int total = 0;
			for (Integer value : modifierGroup.values()) {
				total += value;
			}
			
			if (total == 0) continue;
			
			// Format it
			String value = type.formatValue(total, false);
			
			// Add main line
			if (value == null) {
				lines.add(modifierNamePrefix + name);
			} else {
				lines.add(modifierNamePrefix + name + ": " + modifierValuePrefix + value);
			}
			
			// Add any reason lines
			for (Map.Entry<String, Integer> modifier : modifierGroup.entrySet()) {
				String reason = modifier.getKey();
				if (reason == null) continue;
				
				String modValue = type.formatValue(modifier.getValue(), true);
				if (modValue == null) {
					lines.add(modifierReasonPrefix + " (" + reason + ")");
				} else {
					lines.add(modifierReasonPrefix + " " + modValue + " (" + reason + ")");
				}
			}
		}
		
		return lines;
	}
	
	
	private static final HashMap<String, LoreTemplate> templates = new HashMap<>();
	public static LoreTemplate getLoreTemplate(String name) {
		return templates.get(name);
	}
	public static void registerTemplateFile(ConfigurationSection templateConfig) {
		for (String key : templateConfig.getKeys(false)) {
			templates.put(key, new LoreTemplate(templateConfig.getConfigurationSection(key)));
		}
	}
	
}
