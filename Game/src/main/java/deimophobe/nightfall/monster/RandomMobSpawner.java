package deimophobe.nightfall.monster;

import com.google.common.collect.Sets;
import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.monster.mob.Mob;

import java.util.Set;

/**
 * Created by Deimophobe on 19/06/18.
 */
public class RandomMobSpawner<T extends Mob> implements MobCreator<T> {
	private final String name;
	private final Set<MobCreator<? extends T>> creators;
	
	@SafeVarargs
	public RandomMobSpawner(String name, MobCreator<? extends T>... creators) {
		this.name = name;
		this.creators = Sets.newHashSet(creators);
	}
	
	@Override
	public T createMob(MonsterPlayer monster) {
		MobCreator<? extends T> creator = Misc.getRandom(creators);
		return creator.createMob(monster);
	}
	
	@Override
	public String getName() {
		return name;
	}
}
