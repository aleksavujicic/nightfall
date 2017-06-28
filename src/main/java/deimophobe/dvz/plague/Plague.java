package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.dwarf.Dwarf;

import java.util.Set;

/**
 * Created by Deimophobe on 15/04/17.
 */
public interface Plague {
	void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt);
	void forceEnd();
	
	static Plague getRandomPlague() {
		return PlagueType.getRandomPlague();
	}
	
	default void notifyEnd() {
		Game.getGame().notifyPlagueFinish();
	}
}
