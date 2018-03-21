package deimophobe.nightfall.command.iterable;

import deimophobe.nightfall.monster.MonsterPlayer;

/**
 * Created by Deimophobe on 21/03/18.
 */
public class MonsterIterable extends IterableWrapper<MonsterPlayer> {
	public MonsterIterable(Iterable<MonsterPlayer> iterable) {
		super(iterable);
	}
}
