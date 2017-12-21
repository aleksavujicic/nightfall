package deimophobe.nightfall.common.items.modifiers;

import deimophobe.nightfall.common.Misc;

/**
 * Created by Deimophobe on 27/04/17.
 */
public class ItemModifier {
	private final int value;
	private final String reason;
	
	public ItemModifier(int value, String reason) {
		this.value = value;
		this.reason = reason;
	}
	
	public int getValue() {
		return value;
	}
	
	public String getReason() {
		return reason;
	}
}
