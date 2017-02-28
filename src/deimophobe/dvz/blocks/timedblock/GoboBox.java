package deimophobe.dvz.blocks.timedblock;

import deimophobe.dvz.GamePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 28/02/17.
 */
public class GoboBox extends TimedBlock {
	public GoboBox(Block block, int lifeTime) {
		super(block, Material.ENDER_STONE, lifeTime);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		if (!cancelled) {
			// create explosion
		}
	}
	
	@Override
	void onHit(GamePlayer player) {}
}
