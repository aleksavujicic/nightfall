package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.blocks.BlockConverter;
import deimophobe.nightfall.damage.DamageManager;
import deimophobe.nightfall.damage.type.CustomDamageType;
import deimophobe.nightfall.dwarf.Dwarf;
import deimophobe.nightfall.dwarf.DwarfManager;
import deimophobe.nightfall.entity.GameEntity;
import deimophobe.nightfall.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
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

			DamageManager.getManager().AOEDamage(DwarfManager.getManager().getDwarves(), getPlacer(),
					CustomDamageType.GOBO_BOX_EXPLOSION, centerLoc, 5, 40, 3);
		}
	}
	
	@Override
	void onHit(GamePlayer player) {
		if (player instanceof Dwarf)
			this.cancel();
	}
}
