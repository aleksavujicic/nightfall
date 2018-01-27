package deimophobe.nightfall.common.items.lore;

import deimophobe.nightfall.common.items.modifiers.ItemModifier;
import deimophobe.nightfall.common.items.modifiers.ItemModifierType;
import org.bukkit.ChatColor;

import java.util.*;

/**
 * Created by Deimophobe on 21/04/17.
 */
public class Lore implements Cloneable {
	
	private final LoreTemplate loreTemplate;
	
	private final String name;
	
	private final StringBuilder loreText;
	private final Map<String, Integer> variablePointers;
	
	public Lore(LoreTemplate loreTemplate, String name, StringBuilder loreText, Map<String, Integer> variablePointers) {
		this.loreTemplate = loreTemplate;
		
		this.name = name;
		
		this.loreText = new StringBuilder(loreText);
		this.variablePointers = new HashMap<>(variablePointers);
	}
	
	public Lore(LoreTemplate loreTemplate, String name, Map<String, String> loreSections) {
		this.loreTemplate = loreTemplate;
		
		this.name = name;
		
		this.loreText = loreTemplate.generateLoreText(loreSections);
		this.variablePointers = new HashMap<>();
		findVariables();
	}
	
	private void findVariables() {
		varfinder: for (int i = 0; i< loreText.length(); i++) {
			if (loreText.charAt(i) != '$') continue;
			if (loreText.charAt(i-1) == '\\') continue;
			
			// Found $ sign - search ahead to find end of var name
			int j = i;
			do {
				j++;
				if (j >= loreText.length()) break varfinder;
			} while (Character.isLetter(loreText.charAt(j)));
			
			// Found end of var name
			// Add to varpointers and remove '$name' text
			String name = loreText.substring(i+1, j);
			variablePointers.put(name, i);
			loreText.replace(i, j, "");
		}
	}
	
	public void interpolateVariables(Map<String, String> variables) {
		for (Map.Entry<String, String> entry : variables.entrySet()) {
			String varName = entry.getKey();
			String varValue = entry.getValue();
			
			interpolateVariable(varName, varValue);
		}
	}
	
	public void interpolateVariable(String name, String value) {
		// If lore does not contain variable, then skip
		if (!variablePointers.containsKey(name)) return;
		
		// Otherwise insert it into the lore
		int varPos = variablePointers.get(name);
		loreText.insert(varPos, value);
		
		// Offset any pointers that occur after
		int varLength = value.length();
		variablePointers.replaceAll((k, v) -> (v < varPos ? v : v + varLength));
	}
	
	
	@Override
	public Lore clone() {
		return new Lore(loreTemplate, name, loreText, variablePointers);
	}
	
	
	
	public String createName() {
		return ChatColor.translateAlternateColorCodes('&',loreTemplate.getName(name));
	}
	
	public List<String> createLore(SortedMap<ItemModifierType, Set<ItemModifier>> modifiers, List<String> errors) {
		List<String> lore = new ArrayList<>();
		
		// Add errors
		for (String error : errors) {
			lore.add("" + ChatColor.RED + ChatColor.BOLD  + "ERROR: " + ChatColor.RED + error);
		}
		
		
		// Add lore text
		String text = loreText.toString();
		text = ChatColor.translateAlternateColorCodes('&', text);
		text.replaceAll("\n\n+", "\n\n");
		
		lore.addAll(Arrays.asList(text.split("\n")));
		
		// Add modifier text
		List<String> modifierLines = loreTemplate.generateAttributeText(modifiers);
		if (!modifierLines.isEmpty()) {
			lore.add("");
			lore.addAll(modifierLines);
		}
		
		return lore;
	}
}
