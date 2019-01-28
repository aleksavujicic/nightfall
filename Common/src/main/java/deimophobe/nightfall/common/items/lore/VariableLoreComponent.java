package deimophobe.nightfall.common.items.lore;

import deimophobe.nightfall.common.NightfallCommonPlugin;

/**
 * Created by Deimophobe on 10/05/18.
 */
class VariableLoreComponent implements LoreComponent {
	
	private final String name;
	private final String prefix;
	
	private String contents = null;
	
	VariableLoreComponent(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
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
		
		return prefix + contents + "&r";
	}
	
	@Override
	public LoreComponent copy() {
		VariableLoreComponent copy = new VariableLoreComponent(name, prefix);
		copy.contents = this.contents;
		return copy;
	}
}
