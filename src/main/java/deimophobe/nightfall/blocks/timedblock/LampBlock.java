package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
}
