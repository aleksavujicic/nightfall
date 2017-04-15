package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
public abstract class AbstractPlague extends Plague {
	protected Set<Dwarf> plagueables;
	protected int toKill;
	
	@Override
	public void startPlague(Set<Dwarf> plagueables, int killAmt) {
		this.plagueables = plagueables;
		this.toKill = killAmt;
	}
	
	@Override
	public void forceEnd() {
		Iterator<Dwarf> iter = plagueables.iterator();
		while (iter.hasNext() && toKill > 0) {
			iter.next().kill();
			toKill--;
		}
		notifyEnd();
	}
	
	
	protected void removeDwarf(Dwarf dwarf) {
		toKill--;
		plagueables.remove(dwarf);
	}
	
	protected void killDwarf(Dwarf dwarf) {
		dwarf.kill();
		removeDwarf(dwarf);
	}
}
