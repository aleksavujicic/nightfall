package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 23/01/17.
 */
public class LampBlock extends TimedBlock {
	
	public LampBlock(Location loc, int lifeTime, GameEntity placer) {
		super(loc, Material.JACK_O_LANTERN, lifeTime, placer);
	}
	
	public LampBlock(Block block, int lifeTime, GameEntity placer) {
		super(block, Material.JACK_O_LANTERN, lifeTime, placer);
	}
	
	@Override
	void onPlace() {
		super.onPlace();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.3, 0.4, 0.4, 0, new MaterialData(Material.JACK_O_LANTERN));
		world.playSound(center, Sound.BLOCK_WOOD_PLACE, 1f, 1f);
		world.playSound(center, Sound.ENTITY_ILLUSION_ILLAGER_PREPARE_BLINDNESS, 0.6f, 1.5f);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		super.onDestroy(cancelled);
		if (!cancelled) {
			Location center = block.getLocation().add(0.5, 0.5, 0.5);
			World world = center.getWorld();
			world.spawnParticle(Particle.BLOCK_CRACK, center, 15, 0.3, 0.3, 0.3, 0, new MaterialData(block.getType()));
			world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
		}
	}
}
