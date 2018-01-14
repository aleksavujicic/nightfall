package deimophobe.nightfall.blocks.timedblock;

import deimophobe.nightfall.entity.GameEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 14/01/18.
 */
public class IceSlab extends TimedBlock {
	public IceSlab(Block block, GameEntity placer) {
		super(block, Material.ICE, 20*20, placer);
	}
	
	@Override
	void onDestroy(boolean cancelled) {
		block.setType(Material.AIR);
	}
}
