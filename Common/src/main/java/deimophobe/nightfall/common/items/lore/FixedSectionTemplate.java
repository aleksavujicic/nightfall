package deimophobe.nightfall.common.items.lore;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
class FixedSectionTemplate implements SectionTemplate {
	private final String string;
	
	FixedSectionTemplate(String string) {
		this.string = string;
	}
	
	@Override
	public Section createSection(Map<String, String> loreSections) {
		return new Section(this, string);
	}
	
	@Override
	public String formatString(String rawString) {
		return rawString;
	}
}
