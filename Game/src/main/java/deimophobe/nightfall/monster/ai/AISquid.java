package deimophobe.nightfall.monster.ai;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.dwarf.Dwarf;
import org.bukkit.Location;
import org.bukkit.entity.Squid;

import java.util.function.Consumer;

/**
 * Created by Deimophobe on 24/12/18.
 */
public class AISquid extends AIEntity<Squid> {
	
	private static final Consumer<Squid> INITIALISER = (squid) -> {
		squid.setMaximumAir(60*20);
		squid.setRemainingAir(60*20);
		
		Location location = squid.getLocation();
		float yaw = Misc.randomFloat(0, 360f);
		location.setYaw(yaw);
		squid.teleport(location);
	};
	
	AISquid(Location location, String name, Dwarf target) {
		super(location, name, target, Squid.class, INITIALISER, false);
	}
	
	@Override
	protected void naturalUpdate() {
		super.naturalUpdate();
		
		if (getTarget() != null) {
			resetInactivity();
		}
	}
	
	private static Location randomiseYaw(Location location) {
		float yaw = Misc.randomFloat(0, (float) (2 * Math.PI));
		Location copy = location.clone();
		copy.setYaw(yaw);
		return copy;
	}
}
