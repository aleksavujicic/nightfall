package deimophobe.dvz.items.lore;

import org.bukkit.ChatColor;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
class SectionComponent implements LoreComponent {
	
	private final String name;
	private final String prefix;
	
	SectionComponent(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}
	
	@Override
	public String toString(Map<String, String> sections) {
		String section = prefix + sections.get(name);
		section = section.replaceAll("&r", "&r" + prefix);
		section = section.replaceAll("\n", "\n" + prefix);
		
		return section;
	}
}
