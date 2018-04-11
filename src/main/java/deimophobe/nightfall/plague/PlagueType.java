package deimophobe.nightfall.plague;

import deimophobe.nightfall.common.Misc;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Deimophobe on 29/06/17.
 */
public enum PlagueType {
	ZOMBIE(ZombiePlague.class, true),
	INSTA(InstaPlague.class, false),
	TWINS(TwinsPlague.class, true),
	ASSASSIN(AssassinPlague.class, true),
	STORM(StormPlague.class, true)
	
	;
	
	private final Class<? extends Plague> plagueClass;
	private final boolean active;
	
	PlagueType(Class<? extends Plague> plagueClass) {
		this(plagueClass, true);
	}
	
	PlagueType(Class<? extends Plague> plagueClass, boolean active) {
		this.plagueClass = plagueClass;
		
		// Test to see if it can create a plague.
		createPlague();
		this.active = active;
	}
	
	public Plague createPlague() {
		try {
			return plagueClass.getDeclaredConstructor().newInstance();
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
	
	public static PlagueType getRandomPlagueType() {
		Set<PlagueType> validTypes = new HashSet<>();
		for (PlagueType type : values()) {
			if (type == PlagueType.ASSASSIN && Plague.getAmountToKill(false) < 2) {
				continue;
			} else if (type.active) {
				validTypes.add(type);
			}
		}

		return Misc.getRandom(validTypes);
	}
}
