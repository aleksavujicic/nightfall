package deimophobe.nightfall.dwarf.consumable;

import deimophobe.nightfall.dwarf.Dwarf;

/**
 * Created by Deimophobe on 6/06/18.
 */
public class ConsumeResult {
	public static final ConsumeResult SUCCESS = new ConsumeResult(null, true, 10);
	public static final ConsumeResult FAILURE = new ConsumeResult(null, false, 0);
	
	private final String message;
	private final boolean consumeItem;
	private final int cooldownTime;
	
	public static ConsumeResult successfulWithDuration(int duration) {
		return new ConsumeResult(null, true, duration);
	}
	
	public static ConsumeResult failedResultWithMessage(String message) {
		return new ConsumeResult(message, false, 0);
	}
	
	ConsumeResult(String message, boolean consumeItem, int cooldownTime) {
		this.message = message;
		this.consumeItem = consumeItem;
		this.cooldownTime = cooldownTime;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void displayMessage(Dwarf dwarf) {
		if (message != null) dwarf.sendTitleMessage(message);
	}
	
	public boolean shouldConsumeItem() {
		return consumeItem;
	}
	
	public int getCooldownTime() {
		return cooldownTime;
	}
}
