package deimophobe.nightfall.common.items.lore;

import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * Created by Deimophobe on 21/04/17.
 */
public class Lore implements Cloneable {
	
	private final LoreTemplate loreTemplate;
	
	private String name;
	private final List<Section> sections;
	
	public Lore(LoreTemplate loreTemplate, String name, List<Section> sections) {
		this.loreTemplate = loreTemplate;
		
		this.name = name;
		List<Section> newSections = new ArrayList<>();
		for (Section section : sections) {
			Section copied = section.clone();
			newSections.add(copied);
		}
		this.sections = Collections.unmodifiableList(newSections);
	}
	
	public Lore(LoreTemplate loreTemplate, String name, Map<String, String> loreSections) {
		this.loreTemplate = loreTemplate;
		
		this.name = name;
		List<Section> sections = loreTemplate.createSections(loreSections);
		this.sections = Collections.unmodifiableList(sections);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void applyVariable(String name, String value) {
		for (Section section : sections) {
			section.applyVariable(name, value);
		}
	}
	
	
	@Override
	public Lore clone() {
		return new Lore(loreTemplate, name, sections);
	}
	
	
	
	public String createName() {
		return ChatColor.translateAlternateColorCodes('&',loreTemplate.formatName(name));
	}
	
	public List<String> createLore(Map<ItemModifierType, Map<String, Integer>> modifiers, List<String> errors) {
		List<String> lore = new ArrayList<>();
		
		// Add errors
		for (String error : errors) {
			error = "" + ChatColor.RED + ChatColor.BOLD  + "ERROR: " + ChatColor.RED + error;
			boolean firstLine = true;
			for (String line : WordUtils.wrap(error, 60).split("\n")) {
				if (firstLine) {
					lore.add(line);
					firstLine = false;
				} else {
					lore.add("  " + ChatColor.RED + line);
				}
			}
			//lore.add("" + ChatColor.RED + ChatColor.BOLD  + "ERROR: " + ChatColor.RED + error);
		}
		
		
		// Add lore text
		for (Section section : sections) {
			List<String> lines = section.createLoreLines();
			lore.addAll(lines);
		}
		
		// Add modifier text
		List<String> modifierLines = loreTemplate.generateAttributeText(modifiers);
		if (!modifierLines.isEmpty()) {
			lore.add("");
			lore.addAll(modifierLines);
		}
		
		// If there is nothing in here, then just return null to remove lore from item.
		for (String line : lore) {
			// Contains non-whitespace - return lore
			if (line.matches(".*\\S.*")) return lore;
		}
		return null;
	}
}
