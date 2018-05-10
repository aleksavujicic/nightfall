package deimophobe.nightfall.common.items.lore;

import deimophobe.nightfall.common.NightfallCommonPlugin;

/**
 * Created by Deimophobe on 10/05/18.
 */
class VariableLoreComponent implements LoreComponent {
	
	private final String name;
	private String contents = null;
	
	VariableLoreComponent(String name) {
		this.name = name;
	}
	
	@Override
	public void applyVariable(String variableName, String value) {
		if (name.equals(variableName)) {
			contents = value;
		}
	}
	
	@Override
	public String createString() {
		if (contents == null) {
			NightfallCommonPlugin.getPlugin().getLogger().warning("Creating variable string (name '" + name + "') with no value.");
			return "";
		}
		
		return contents;
	}
}
