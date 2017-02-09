package deimophobe.dvz.blocks.timedblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 23/01/17.
 */
public class LampBlock extends TimedBlock {
	
	public LampBlock(Location loc, int lifeTime) {
		super(loc, Material.JACK_O_LANTERN, lifeTime);
	}
	
	public LampBlock(Block block, int lifeTime) {
		super(block, Material.JACK_O_LANTERN, lifeTime);
	}
}
