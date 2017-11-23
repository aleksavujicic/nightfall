package deimophobe.nightfall.dwarf.kit;

/**
 * Created by Deimophobe on 19/03/17.
 */
public enum KitGiveType {
	START,
	
	SWORD,
	BOW,
	ALE,
	
	PICK(true),
	AXE(true),
	SHOVEL(true),
	
	ARTHEA_SPECIAL;
	
	
	// Store these values in one common array
	// Calling values() in Java initialises a new array each time.
	private static final KitGiveType[] VALUES = values();
	public static KitGiveType[] fixedValues() {
		return VALUES;
	}
	
	
	
	private final boolean multiPickup;
	public int getMaxDelay() {
		if (multiPickup)
			return 15*20;
		else
			return Integer.MAX_VALUE;
	}
	
	KitGiveType() {
		this(false);
	}
	
	KitGiveType(boolean multiPickup) {
		this.multiPickup = multiPickup;
	}
}
