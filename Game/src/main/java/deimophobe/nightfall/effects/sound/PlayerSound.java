package deimophobe.nightfall.effects.sound;

import deimophobe.nightfall.game.entity.GamePlayer;
import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 6/07/17.
 */
public interface PlayerSound {
	void playSound(Player player);
	
	default void playSound(GamePlayer player) {
		playSound(player.getPlayer());
	}
}
