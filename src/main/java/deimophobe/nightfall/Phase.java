package deimophobe.nightfall;

/**
 * Created by Deimophobe on 15/01/17.
 */
public enum Phase {
	STARTING,
	BUILD,
	PLAGUE,
	GAME,
	END
	
	;
	
	public boolean hasGameStarted() {
		return this == GAME || this == END;
	}
	
	public boolean isBefore(Phase phase) {
		return this.ordinal() < phase.ordinal();
	}
	
	public boolean isAfter(Phase phase) {
		return this.ordinal() > phase.ordinal();
	}
}
