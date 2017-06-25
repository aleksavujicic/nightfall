package deimophobe.dvz.monster.upgrade;

/**
 * Created by Deimophobe on 25/02/17.
 */
@Deprecated
public enum GlobalUpgrade {
	KRUNGOR,
	;
	
	private boolean unlocked = false;
	
	public void unlock() {
		unlocked = true;
	}
	public boolean isUnlocked() {
		return unlocked;
	}
	
	public static void reset() {
		for (GlobalUpgrade gu : values())
			gu.unlocked = false;
	}
}
