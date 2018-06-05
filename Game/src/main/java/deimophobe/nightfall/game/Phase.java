package deimophobe.nightfall.game;

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
	
	public boolean haveMonstersBeenReleased() {
		return this == GAME || this == END;
	}
	
	public boolean isBefore(Phase phase)     { return this.ordinal() <  phase.ordinal(); }
	public boolean isOrIsBefore(Phase phase) { return this.ordinal() <= phase.ordinal(); }
	public boolean isAfter(Phase phase)      { return this.ordinal() >  phase.ordinal(); }
	public boolean isOrIsAfter(Phase phase)  { return this.ordinal() >= phase.ordinal(); }
	
	public Phase previousPhase() {
		int ord = ordinal();
		if (ord == 0) {
			return null;
		} else {
			return values()[ord-1];
		}
	}
	
	public Phase nextPhase() {
		int ord = ordinal();
		Phase[] vals = values();
		if (ord == vals.length - 1) {
			return null;
		} else {
			return vals[ord+1];
		}
	}
}
