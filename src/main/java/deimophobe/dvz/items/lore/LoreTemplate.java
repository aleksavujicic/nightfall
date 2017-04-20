package deimophobe.dvz.items.lore;

import com.sun.org.apache.xerces.internal.xs.StringList;
import deimophobe.dvz.Misc;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deimophobe on 15/04/17.
 */
public class LoreTemplate {
	
	private final String namePrefix;
	private final List<LoreComponent> components;
	
	private final String attrNamePrefix;
	private final String attrValuePrefix;
	private final String attrReasonPrefix;
	
	private LoreTemplate(ConfigurationSection config) {
		namePrefix = config.getString("nameprefix");
		
		components = new ArrayList<>();
		List<String> lore = config.getStringList("lore");
		String longLore = String.join("\n", lore);
		
		
		// Find sections
		int prevMatch = 0;
		sectionFinder: for (int i=0; i<longLore.length(); i++) {
			if (longLore.charAt(i) != '$') continue;
			if (longLore.charAt(i-1) == '\\') continue;
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
		
		attrNamePrefix = config.getString("attributes.name");
		attrValuePrefix = config.getString("attributes.value");
		attrReasonPrefix = config.getString("attributes.reason");
	}
	
	public String getName(String name) {
		return ChatColor.translateAlternateColorCodes('&',namePrefix + name);
	}
	
	public List<String> generateLore(Map<String, String> sections, Map<String, Object> parameters) {
		StringBuilder builder = new StringBuilder();
		for (LoreComponent component : components)
			builder.append(component.toString(sections));
		
		// Find and replace variables
		varfinder: for (int i=0; i<builder.length(); i++) {
			if (builder.charAt(i) != '$') continue;
			if (builder.charAt(i-1) == '\\') continue;
			
			int j = i;
			do {
				j++;
				if (j >= builder.length()) break varfinder;
			} while (Character.isLetter(builder.charAt(j)));
			
			String name = builder.substring(i+1, j);
			Object value = parameters.get(name);
			String valueString;
			if (value == null) valueString = "";
			else valueString = value.toString();
			
			builder.replace(i, j, valueString);
		}
		String lore = builder.toString();
		lore = ChatColor.translateAlternateColorCodes('&', lore);
		
		String[] splitLore = lore.split("\n");
		return Arrays.asList(splitLore);
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
	
	public static final String DWARF = "dwarf";
	public static final String DWARF_HERO = "dwarf-hero";
	public static final String LOADOUT = "loadout";
	public static final String MOB = "monster";
	public static final String MOB_HER = "monster-hero";
}
