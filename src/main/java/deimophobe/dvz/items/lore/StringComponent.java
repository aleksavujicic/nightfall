package deimophobe.dvz.items.lore;

import java.util.Map;

/**
 * Created by Deimophobe on 15/04/17.
 */
class StringComponent implements LoreComponent {
	private final String string;
	
	StringComponent(String string) {
		this.string = string;
	}
	
	@Override
	public String toString(Map<String, String> sections) {
		return string;
	}
}
