package deimophobe.nightfall.effects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

/**
 * Created by Deimophobe on 10/03/17.
 */
interface EffectMaker {
	
	void playEffect(Location location);
	default void playEffect(Block block) {
		playEffect(block.getLocation().add(0.5, 0.5, 0.5));
	}
	default void playEffect(BlockState block) {
		playEffect(block.getLocation().add(0.5, 0.5, 0.5));
	}
}
