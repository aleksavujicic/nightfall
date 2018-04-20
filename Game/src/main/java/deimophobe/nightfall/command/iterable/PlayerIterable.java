package deimophobe.nightfall.command.iterable;

import org.bukkit.entity.Player;

/**
 * Created by Deimophobe on 21/03/18.
 */
public class PlayerIterable extends IterableWrapper<Player> {
	public PlayerIterable(Iterable<Player> iterable) {
		super(iterable);
	}
}
