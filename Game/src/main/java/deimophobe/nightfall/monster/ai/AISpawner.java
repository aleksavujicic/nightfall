package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.util.Weightable;
import org.bukkit.Location;


/**
 * Created by Deimophobe on 13/05/18.
 */
interface AISpawner extends Weightable {
	/**
	 * Spawn the AIs at a certain spot.
	 * @param location Location to spawn the AIs.
	 * @param target Dwarf to target (may be null).
	 * @return The life cost to the AISpawnLocation.
	 */
	int spawn(Location location, Dwarf target);
}
