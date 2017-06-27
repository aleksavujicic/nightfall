package deimophobe.dvz.plague;

import deimophobe.dvz.Game;
import deimophobe.dvz.Misc;
import deimophobe.dvz.dwarf.Dwarf;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 15/04/17.
 */
public abstract class Plague {
	public abstract void startPlague(Set<Dwarf> plagueables, Set<Dwarf> plagued, int killAmt);
	public abstract void forceEnd();
	
	protected final void notifyEnd() {
		Game.getGame().notifyPlagueFinish();
	}
	
	
	
	public static Plague getRandomPlague() {
		return PlagueType.getRandomPlague();
	}
	
	private enum PlagueType {
		ZOMBIE(ZombiePlague.class, true),
		INSTA(InstaPlague.class, false),
		DEATH(DeathPlague.class, false)
		
		;
		
		private final Plague plague;
		private final boolean active;
		
		PlagueType(Class<? extends Plague> plagueClass) {
			this(plagueClass, true);
		}
		PlagueType(Class<? extends Plague> plagueClass, boolean active) {
			
			this.active = active;
			
			try {
				this.plague = plagueClass.getDeclaredConstructor().newInstance();
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("Unable to find constructor for plague object '" + name() + "'", e);
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Failed to access constructor of plague object '" + name() + "'", e);
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Cannot create abstract plague object '" + name() + "'", e);
			} catch (InvocationTargetException e) {
				throw new IllegalArgumentException("Exception thrown in constructor of plague object '" + name() + "'", e);
			}
		}
		
		public Plague getPlague() {
			return plague;
		}
		
		public static Plague getRandomPlague() {
			Set<PlagueType> validTypes = new HashSet<>();
			for (PlagueType type : values())
				if (type.active)
					validTypes.add(type);
			
			return Misc.getRandom(validTypes).getPlague();
		}
	}
}
