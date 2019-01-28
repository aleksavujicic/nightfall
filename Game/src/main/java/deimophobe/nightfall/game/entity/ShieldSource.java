package deimophobe.nightfall.game.entity;

/**
 * Represents the source of a shield.
 * Note the order represents which shields will get removed first.
 *
 * Created by Deimophobe on 6/10/18.
 */
public enum ShieldSource {
	COMMAND,
	
	SHIELD_ALE(4),
	AEGIS(5),
	BOLSTER(3),
	DIAMOND_ORE(1),
	RESURRECTION(2),
	
	;
	
	private final int maxHearts;
	
	ShieldSource() {
		this.maxHearts = Integer.MAX_VALUE;
	}
	ShieldSource(int maxHearts) {
		this.maxHearts = maxHearts;
	}
	
	public int getMaxHearts() {
		return maxHearts;
	}
}
