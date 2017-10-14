package deimophobe.nightfall.plague;

import deimophobe.nightfall.dwarf.Dwarf;

import java.util.Set;

/**
 * Created by Deimophobe on 9/03/17.
 */
class InstaPlague extends AbstractPlague {
	@Override
	public void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt) {
		super.startPlague(plagueables, plagued, killAmt);
		for (Dwarf dwarf : plagued)
			killDwarf(dwarf);
		forceEnd();
	}
}
