package deimophobe.nightfall.dwarf.light;

/**
 * Created by Deimophobe on 8/12/18.
 */
public interface Vision {
	void increaseVision(int amount);
	void forceBlind(int durationLeft);
	void forceBlind();
}
