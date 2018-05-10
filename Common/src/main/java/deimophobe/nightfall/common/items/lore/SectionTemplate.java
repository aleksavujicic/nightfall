package deimophobe.nightfall.common.items.lore;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
interface SectionTemplate {
	Section createSection(Map<String, String> loreSections);
	String formatString(String rawString);
}
