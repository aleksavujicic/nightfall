package deimophobe.nightfall.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/**
 * Created by Deimophobe on 10/03/17.
 */
public class ArmourCloudMaker implements EffectMaker {
	@Override
	public void playEffect(Location location) {
		World world = location.getWorld();
		world.playSound(location, "block.anvil.land", 0.5f, 0.5f);
		world.spawnParticle(Particle.CLOUD, location, 20, 0.5, 0.5, 0.5, 0);
	}
}
