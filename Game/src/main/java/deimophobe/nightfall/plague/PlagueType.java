package deimophobe.nightfall.plague;

import deimophobe.nightfall.common.Misc;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by Deimophobe on 29/06/17.
 */
public enum PlagueType {
	ZOMBIE(ZombiePlague::new, true),
	INSTA(InstaPlague::new, false),
	TWINS(TwinsPlague::new, true),
	ASSASSIN(AssassinPlague::new, () -> Plague.getAmountToKill(false) > 2),
	STORM(StormPlague::new, true)
	
	;
	
	private final Supplier<? extends Plague> plagueCreator;
	private final Supplier<Boolean> active;
	
	
	PlagueType(Supplier<? extends Plague> plagueCreator, boolean active) {
		this.plagueCreator = plagueCreator;
		this.active = () -> active;
	}
	
	PlagueType(Supplier<? extends Plague> plagueCreator, Supplier<Boolean> active) {
		this.plagueCreator = plagueCreator;
		this.active = active;
	}
	
	public Plague createPlague() {
		return plagueCreator.get();
	}
	
	public static PlagueType getRandomPlagueType() {
		Set<PlagueType> validTypes = new HashSet<>();
		for (PlagueType type : values()) {
			if (type.active.get()) {
				validTypes.add(type);
			}
		}

		return Misc.getRandom(validTypes);
	}
}
