package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.game.GameEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 23/01/17.
 */
public class LampBlock extends TimedBlock {
	
	private final boolean moreEffects;
	
	public LampBlock(Block block, int lifeTime, GameEntity placer, boolean moreEffects) {
		super(block, Material.JACK_O_LANTERN, lifeTime, placer);
		this.moreEffects = moreEffects;
	}
	
	@Override
	void onPlace() {
		super.onPlace();
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 20, 0.4, 0.4, 0.4, 0, new MaterialData(Material.JACK_O_LANTERN));
		world.playSound(center, Sound.BLOCK_WOOD_PLACE, 1f, 1f);
		if (moreEffects) {
			world.spawnParticle(Particle.FLAME, center, 20, 0.4, 0.4, 0.4, 0.01);
			world.playSound(center, Sound.ENTITY_ILLUSION_ILLAGER_PREPARE_BLINDNESS, 0.6f, 1.5f);
		}
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		super.onDestroy(cancelled);
		if (!cancelled) {
			Location center = block.getLocation().add(0.5, 0.5, 0.5);
			World world = center.getWorld();
			world.spawnParticle(Particle.BLOCK_CRACK, center, 15, 0.4, 0.4, 0.4, 0, block.getState().getData());
			world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
		}
	}
}
