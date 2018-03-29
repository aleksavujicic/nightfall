package deimophobe.nightfall.effects;

import deimophobe.nightfall.game.GamePlayer;
import org.bukkit.block.Block;

/**
 * Created by Deimophobe on 10/03/17.
 */
public interface PlayerBlockEffectMaker {
	void playEffect(GamePlayer player, Block block);
}
