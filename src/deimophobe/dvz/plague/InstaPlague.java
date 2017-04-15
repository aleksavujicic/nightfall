package deimophobe.dvz.plague;

import deimophobe.dvz.dwarf.Dwarf;

import java.util.Set;

/**
 * Created by Deimophobe on 9/03/17.
 */
class InstaPlague extends AbstractPlague {
	@Override
	public void startPlague(Set<Dwarf> plagueables, int killAmt) {
		super.startPlague(plagueables, killAmt);
		forceEnd();
	}
}
