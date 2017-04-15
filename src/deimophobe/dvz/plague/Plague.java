package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;
import deimophobe.dvz.dwarf.hero.Hero;
import org.bukkit.entity.Zombie;

import java.util.Set;

/**
 * Created by Deimophobe on 15/04/17.
 */
public abstract class Plague {
	public abstract void startPlague(Set<Dwarf> plagueables, int killAmt);
	public abstract void forceEnd();
	
	protected final void notifyEnd() {
		Game.getGame().notifyPlagueFinish();
	}
	
	
	
	public static Plague getRandomPlague() {
		//return PlagueType.ZOMBIE.getPlague();
		return PlagueType.getRandomPlague();
	}
	
	private enum PlagueType {
		ZOMBIE,
		//INSTA,
		DEATH
		;
		
		public Plague getPlague() {
			return getPlague(this);
		}
		
		public static Plague getPlague(PlagueType type) {
			switch (type) {
				case ZOMBIE:
					return new ZombiePlague();
				//case INSTA:
				//	return new InstaPlague();
				case DEATH:
					return new DeathPlague();
			}
			throw new IllegalArgumentException("Unknown plague type: "+ type);
		}
		
		public static Plague getRandomPlague() {
			return getPlague(Misc.getRandom(values()));
		}
	}
}
