package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.PlayerSound;
import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.util.Colour;
import org.bukkit.Location;
import org.bukkit.Particle;

public class FireGoldMineMaker extends SmallGoldMineMaker {
	FireGoldMineMaker(PlayerSound sound) {
		super(null, sound);
	}
	
	@Override
	protected void spawnGoldParticle(Location loc) {
		loc.getWorld().spawnParticle(Particle.FLAME, loc, 1, 0.03 , 0.03, 0.03, 0.03);
	}
}
