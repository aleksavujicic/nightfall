package deimophobe.nightfall.plague;

import deimophobe.nightfall.dwarf.Dwarf;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
public abstract class AbstractPlague implements Plague {
	protected Set<Dwarf> plagueables;
	protected Set<Dwarf> plagued;
	private int toKill;
	
	@Override
	public void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt) {
		this.plagueables = plagueables;
		this.plagued = plagued;
		this.toKill = Math.max(killAmt, plagued.size());
	}
	
	@Override
	public void forceEnd() {
		Iterator<Dwarf> iter = plagued.iterator();
		while (iter.hasNext() && toKill > 0) {
			iter.next().forceKill();
			toKill--;
		}
		iter = plagueables.iterator();
		while (iter.hasNext() && toKill > 0) {
			iter.next().forceKill();
			toKill--;
		}
		notifyEnd();
	}
	
	
	protected void removeDwarf(Dwarf dwarf) {
		toKill--;
		plagued.remove(dwarf);
		plagueables.remove(dwarf);
	}
	
	protected void killDwarf(Dwarf dwarf) {
		dwarf.forceKill();
		removeDwarf(dwarf);
	}
	
	protected boolean isPlaguable(Dwarf dwarf) {
		return plagueables.contains(dwarf);
	}
	
	protected boolean isPlagued(Dwarf dwarf) {
		return plagued.contains(dwarf);
	}
	
	protected int getAmountToKill() {
		return toKill;
	}
}
