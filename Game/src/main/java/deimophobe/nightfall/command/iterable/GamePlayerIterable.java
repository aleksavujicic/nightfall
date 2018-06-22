package deimophobe.nightfall.command.iterable;

import deimophobe.nightfall.game.player.GamePlayer;

/**
 * Created by Deimophobe on 21/03/18.
 */
public class GamePlayerIterable extends IterableWrapper<GamePlayer> {
	public GamePlayerIterable(Iterable<GamePlayer> iterable) {
		super(iterable);
	}
}
