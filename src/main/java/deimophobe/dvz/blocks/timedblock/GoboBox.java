package deimophobe.dvz.blocks.timedblock;

import deimophobe.dvz.DamageType;
import deimophobe.dvz.GamePlayer;
import deimophobe.dvz.blocks.BlockConverter;
import deimophobe.dvz.dwarf.Dwarf;
import org.bukkit.*;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class GoboBox extends TimedBlock {
	private final double power;
	public GoboBox(Block block, int lifeTime, double power) {
		super(block, Material.ENDER_STONE, lifeTime);
		this.power = power;
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		if (!cancelled) {
			Location centerLoc = block.getLocation().add(0.5, 0.5, 0.5);
			World world = centerLoc.getWorld();
			
			BlockConverter.convert(BlockConverter.Type.EXPLOSION, centerLoc, power);
			world.spawnParticle(Particle.EXPLOSION_LARGE, centerLoc, 3, 1, 1, 1);
			world.playSound(centerLoc, "entity.generic.explode", 2, 1);
		}
	}
	
	@Override
	void onHit(GamePlayer player) {
		if (player instanceof Dwarf)
			this.cancel();
	}
}
