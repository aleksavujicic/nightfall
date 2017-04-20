package deimophobe.dvz.effects;

import deimophobe.dvz.Game;
import deimophobe.dvz.GamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 10/03/17.
 */
interface EffectMaker {
	
	void playEffect(Location location);
	
	default void playEffect(GamePlayer player) {
		playEffect(player.getEyeLocation());
	}
	default void playEffect(Block block) {
		playEffect(block.getLocation().add(0.5, 0.5, 0.5));
	}
	default void playEffect(BlockState block) {
		playEffect(block.getLocation().add(0.5, 0.5, 0.5));
	}
}
