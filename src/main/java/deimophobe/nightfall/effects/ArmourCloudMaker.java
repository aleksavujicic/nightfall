package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.Sounds;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

/**
 * Created by Deimophobe on 10/03/17.
 */
class ArmourCloudMaker implements LocationEffectMaker {
	@Override
	public void playEffect(Location location) {
		World world = location.getWorld();
		Sounds.DWARF_MINE_ARMOUR.playSound(location);
		world.spawnParticle(Particle.CLOUD, location, 20, 0.5, 0.5, 0.5, 0);
	}
}
