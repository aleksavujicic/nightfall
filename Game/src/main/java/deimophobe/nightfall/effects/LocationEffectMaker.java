package deimophobe.nightfall.effects;

import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 27/10/17.
 */
public interface LocationEffectMaker extends PlayerEffectMaker, BlockEffectMaker {
	void playEffect(Location location);
	
	@Override
	default void playEffect(GamePlayer player) {playEffect(player.getEyeLocation());}
	
	@Override
	default void playEffect(Block block) {playEffect(block.getLocation().add(0.5,0.5,0.5));}
}
