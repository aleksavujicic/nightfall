package deimophobe.nightfall.game.entity;

/**
 * Represents the source of a shield. Meant to be used to easily determine number of
 * max shields, but there are some issues to work out.
 *
 * Eg. 3 Res hearts, 5 Shield ale, so 8 total. Take 1 damage. Which hearts get damaged?
 *
 * Created by Deimophobe on 6/10/18.
 */
public enum ShieldSource {
	DIAMOND_ORE(1),
	RESURRECTION(3),
	SHIELD_ALE(5),
	
	COMMAND,
	
	;
	
	private final int maxHearts;
	
	ShieldSource() {
		this.maxHearts = Integer.MAX_VALUE;
	}
	
	ShieldSource(int maxHearts) {
		this.maxHearts = maxHearts;
	}
}
