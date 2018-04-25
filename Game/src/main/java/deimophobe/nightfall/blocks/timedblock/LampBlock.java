package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.game.GameEntity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

/**
 * Created by Deimophobe on 23/01/17.
 */
public class LampBlock extends DataTimedBlock {
	
	private final boolean moreEffects;
	
	public LampBlock(Block block, int lifetime, GameEntity placer, boolean moreEffects) {
		super(lifetime, block, placer, Material.JACK_O_LANTERN);
		this.moreEffects = moreEffects;
	}
	
	@Override
	public void placeBlock() {
		super.placeBlock();
		
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
	public void unplaceBlock(boolean cancelled) {
		super.unplaceBlock(cancelled);
		if (cancelled) return;
		
		Location center = block.getLocation().add(0.5, 0.5, 0.5);
		World world = center.getWorld();
		world.spawnParticle(Particle.BLOCK_CRACK, center, 15, 0.4, 0.4, 0.4, 0, block.getState().getData());
		world.playSound(center, Sound.BLOCK_STONE_PLACE, 1f, 1f);
	}
}
