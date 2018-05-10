package deimophobe.nightfall.common.items.lore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Deimophobe on 10/05/18.
 */
class Section {
	private static final Pattern VAR_REGEX = Pattern.compile("\\$([a-zA-Z0-9]+)");
	
	private final SectionTemplate template;
	private final List<LoreComponent> components;
	
	Section(SectionTemplate template, String sectionText) {
		this.template = template;
		this.components = new ArrayList<>();
		
		// Find variables and add components as necessary
		Matcher varFinder = VAR_REGEX.matcher(sectionText);
		
		int sectionStart = 0;
		while (varFinder.find()) {
			int sectionEnd = varFinder.start();
			// Add text
			String text = sectionText.substring(sectionStart, sectionEnd);
			LoreComponent textComponent = new FixedLoreComponent(text);
			components.add(textComponent);
			
			// Add variable
			String varName = varFinder.group(1);
			LoreComponent varComponent = new VariableLoreComponent(varName);
			components.add(varComponent);
			
			sectionStart = varFinder.end();
		}
		
		String text = sectionText.substring(sectionStart);
		LoreComponent textComponent = new FixedLoreComponent(text);
		components.add(textComponent);
	}
	
	public void applyVariable(String name, String value) {
		for (LoreComponent lc : components) {
			lc.applyVariable(name, value);
		}
	}
	
	public String createString() {
		StringBuilder sb = new StringBuilder();
		components.forEach(lc -> sb.append(lc.createString()));
		String raw = sb.toString();
		return template.formatString(raw);
	}
	
}
