package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Deimophobe on 7/03/17.
 */
public abstract class Plague {
	protected Set<Dwarf> plagueables;
	protected int toKill;
	
	public void startPlague(Set<Dwarf> plagueables, int killAmt) {
		this.plagueables = plagueables;
		this.toKill = killAmt;
		onStart();
	}
	
	protected abstract void onStart();
	
	public void forceEnd() {
		Iterator<Dwarf> iter = plagueables.iterator();
		while (iter.hasNext() && toKill > 0) {
			iter.next().kill();
			toKill--;
		}
		endPlague();
	}
	
	protected void endPlague() {
		Game.getGame().endPlague();
	}
	
	protected void removeDwarf(Dwarf dwarf) {
		toKill--;
		plagueables.remove(dwarf);
	}
	
	protected void killDwarf(Dwarf dwarf) {
		dwarf.kill();
		removeDwarf(dwarf);
	}
	
	public static Plague getRandomPlague() {
		return Misc.getRandom(Type.values()).getPlague();
	}
	
	enum Type {
		//ZOMBIE { @Override public Plague getPlague() { return new ZombiePlague(); } },
		//INSTA(new InstaPlague()),
		DEATH { @Override public Plague getPlague() { return new DeathPlague(); } },
		;
		
		public abstract Plague getPlague();
	}
}
