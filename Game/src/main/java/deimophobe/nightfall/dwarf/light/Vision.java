package deimophobe.nightfall.dwarf.light;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 8/12/18.
 */
public interface Vision {
	void increaseVision(int amount);
	void forceBlind(int durationLeft);
	void forceBlind();
}
