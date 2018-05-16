package deimophobe.nightfall.common.items.lore;

/**
 * Created by Deimophobe on 10/05/18.
 */
class FixedLoreComponent implements LoreComponent {
	
	private final String content;
	
	FixedLoreComponent(String content) {
		this.content = content;
	}
	
	@Override
	public void applyVariable(String variableName, String value) {
		// Contains no variables - do nothing.
	}
	
	@Override
	public String createString() {
		return content;
	}
	
	@Override
	public FixedLoreComponent copy() {
		return this; // FixedLoreComponent is immutable so this is ok.
	}
}
