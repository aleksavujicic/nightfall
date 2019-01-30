package deimophobe.nightfall.common.items.lore;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
class NamedSectionTemplate implements SectionTemplate {
	
	private final String name;
	private final String prefix;
	private final String defaultText;
	
	NamedSectionTemplate(String name, String prefix, String defaultText) {
		this.name = name;
		this.prefix = prefix;
		this.defaultText = defaultText;
	}
	
	@Override
	public Section createSection(Map<String, String> loreSections, LoreTemplate loreTemplate) {
		String sectionText = getSectionText(loreSections);
		return new Section(loreTemplate, this, sectionText);
	}
	
	@Override
	public String formatString(String rawString) {
		String formattedString = prefix + rawString;
		formattedString = formattedString.replaceAll("&r", "&r" + prefix);
		formattedString = formattedString.replaceAll("(\n+)\\s*", "$1" + prefix);
		
		return formattedString;
	}
	
	private String getSectionText(Map<String, String> sections) {
		String section = sections.get(name);
		if (section == null) return defaultText;
		
		return section;
	}
}
