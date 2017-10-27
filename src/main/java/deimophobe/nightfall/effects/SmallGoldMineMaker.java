package deimophobe.nightfall.effects;

import deimophobe.nightfall.effects.sound.Sounds;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 27/10/17.
 */
class SmallGoldMineMaker implements PlayerBlockEffectMaker {
	
	@Override
	public void playEffect(GamePlayer player, Block block) {
		Sounds.DWARF_MINE_GOLD.playSound(player);
		spawnGoldParticle(block.getLocation().add(0.5,0.5,0.5));
		
		for (int ix = -1; ix <= 1; ix += 2) {
			double x = ix*0.25;
			for (int iy = -1; iy <= 1; iy += 2) {
				double y = iy*0.25;
				for (int iz = -1; iz <= 1; iz += 2) {
					double z = iz*0.25;
					block.getWorld().spawnParticle(Particle.REDSTONE, block.getLocation().add(0.5+x,0.5+y,0.5+z), 0, 250d/256, 250d/256, 10d/256, 1);
				}
			}
		}
	}
	
	protected void spawnGoldParticle(Location loc) {
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 250d/256, 250d/256, 10d/256, 1);
	}
}
