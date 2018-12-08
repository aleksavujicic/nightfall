package deimophobe.nightfall.dwarf.light;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 8/12/18.
 */
class DwarfVision implements Vision {
	private int vision;
	private int blindnessDuration;
	private boolean forceBlindness;
	
	DwarfVision() {
		resetVisionCount();
	}
	
	@Override
	public void increaseVision(int amount) {
		checkArgument(amount >= 0, "Vision increase should be non-negative (got '%s)", amount);
		vision = Math.max(vision, amount); // Should have a way to 'add on' as well as taking max.
	}
	
	@Override
	public void forceBlind(int duration) {
		checkArgument(duration >= 0, "Blindness duration must be non-negative (got '%s)", duration);
		blindnessDuration = Math.max(blindnessDuration, duration);
	}
	
	@Override
	public void forceBlind() {
		forceBlindness = true;
	}
	
	int getVision() {
		return vision;
	}
	int getBlindnessDuration() {
		return blindnessDuration;
	}
	boolean isForceBlind() {
		return forceBlindness;
	}
	
	void resetVisionCount() {
		blindnessDuration = 0;
		vision = 0;
	}
}
