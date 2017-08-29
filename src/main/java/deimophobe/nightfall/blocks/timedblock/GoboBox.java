package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.damage.DamageType;
import deimophobe.nightfall.Explosion;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import org.bukkit.*;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class GoboBox extends TimedBlock {
	private final double power;
	public GoboBox(Block block, int lifeTime, double power, GameEntity placer) {
		super(block, Material.ENDER_STONE, lifeTime, placer);
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

			(new Explosion(this.getPlacer(), DwarfManager.getManager().getGamePlayers(), centerLoc, DamageType.CUSTOM_EXPLOSION, 40, 5, 3)).explode();
		}
	}
	
	@Override
	void onHit(GamePlayer player) {
		if (player instanceof Dwarf)
			this.cancel();
	}
}
