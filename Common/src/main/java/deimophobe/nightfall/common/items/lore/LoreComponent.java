package deimophobe.nightfall.common.items.lore;

/**
 * Created by Deimophobe on 10/05/18.
 */
interface LoreComponent {
	void applyVariable(String variableName, String value);
	String createString();
}
