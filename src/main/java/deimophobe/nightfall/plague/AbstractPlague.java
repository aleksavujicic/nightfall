package deimophobe.nightfall.plague;

import deimophobe.nightfall.damage.GameDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.monster.MonsterManager;

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
			iter.next().instaKill(null, GameDamageType.FORCE_PLAGUED);
			toKill--;
		}
		iter = plagueables.iterator();
		while (iter.hasNext() && toKill > 0) {
			iter.next().instaKill(null, GameDamageType.FORCE_PLAGUED);
			toKill--;
		}
		notifyEnd();
	}
	
	@Override
	public void onDwarfDeath(Dwarf dwarf) {
		removeDwarf(dwarf);
	}
	
	protected void removeDwarf(Dwarf dwarf) {
		toKill--;
		plagued.remove(dwarf);
		plagueables.remove(dwarf);
		MonsterManager.getManager().addPlaguedPlayer(dwarf);
	}
	
	protected void killDwarf(Dwarf dwarf) {
		dwarf.instaKill(null, GameDamageType.FORCE_PLAGUED);
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
