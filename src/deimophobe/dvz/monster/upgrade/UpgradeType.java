package deimophobe.dvz.monster.upgrade;

/**
 * Created by Deimophobe on 24/02/17.
 */
public enum UpgradeType {
	ATTACK,
	HEALTH,
	FURY,
	SPEED;
	
	public static UpgradeType getUpgradeType(String name) {
		return valueOf(name.toUpperCase());
	}
}
