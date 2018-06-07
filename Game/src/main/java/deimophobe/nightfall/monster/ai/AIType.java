package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;

/**
 * Created by Deimophobe on 25/03/18.
 */
public enum AIType {
	ZOMBIE(AIZombie::new),
	FIRE_SKELLY(AIFireSkeleton::new),
	HUSK(AIHusk::new),
	
	;
	
	private final AICreator creator;
	
	AIType(AICreator creator) {
		this.creator = creator;
	}
	
	AIEntity<?> createAI(Location location, String name, Dwarf target) {
		return creator.createAI(location, name, target);
	}
	
	private interface AICreator {
		AIEntity<?> createAI(Location location, String name, Dwarf target);
	}
}
