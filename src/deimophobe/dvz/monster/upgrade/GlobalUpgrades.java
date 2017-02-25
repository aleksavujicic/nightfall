package deimophobe.dvz.monster.upgrade;

/**
 * Created by Deimophobe on 25/02/17.
 */
public enum GlobalUpgrades {
	KRUNGOR,
	;
	
	private boolean unlocked = false;
	
	public void unlock() {
		unlocked = true;
	}
	public boolean isUnlocked() {
		return unlocked;
	}
}
