package deimophobe.nightfall.monster;

import deimophobe.nightfall.monster.mob.Mob;

import java.util.function.Function;

/**
 * Created by Deimophobe on 19/06/18.
 */
public class SimpleMobCreator<T extends Mob> implements MobCreator<T> {
	private final String name;
	private final Function<MonsterPlayer, T> creator;
	
	public SimpleMobCreator(String name, Function<MonsterPlayer, T> creator) {
		this.name = name;
		this.creator = creator;
	}
	
	@Override
	public T createMob(MonsterPlayer monster) {
		return creator.apply(monster);
	}
	
	@Override
	public String getName() {
		return name;
	}
}
