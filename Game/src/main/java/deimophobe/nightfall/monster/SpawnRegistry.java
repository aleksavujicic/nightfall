package deimophobe.nightfall.monster;

import deimophobe.nightfall.common.Misc;
import deimophobe.nightfall.monster.mob.MobType;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 19/06/18.
 */
public class SpawnRegistry {
	public static SpawnRegistry getRegistry() { return MonsterManager.getManager().getRegistry(); }
	
	private final Map<String,MobCreator<?>> creators;
	
	SpawnRegistry() {
		this.creators = new HashMap<>();
		for (MobType type : MobType.getSpawnableMobs()) {
			register(type);
		}
		
		register(new RandomMobCreator<>("magi", MobType.FIRE_MAGI, MobType.ICE_MAGI, MobType.THUNDER_MAGI));
		//register(new RandomMobCreator<>("zombie", MobType.ZOMBIE));
		register(new SimpleMobCreator<>("primary", mp -> mp.getPrimaryMob().createMob(mp)));
	}
	
	private void register(MobCreator<?> creator) {
		String name = creator.getName();
		checkArgument(!creators.containsKey(name), "Cannot insert creator with name '%s' as it already exists.", name);
		creators.put(name, creator);
		
		Misc.registerPermissionIfNotRegistered(creator.getPermission());
	}
	
	
	public Set<String> getValidCreators(Permissible permissible) {
		Set<String> validCreators = new HashSet<>();
		for (MobCreator<?> creator : creators.values()) {
			Permission perm = creator.getPermission();
			if (!permissible.hasPermission(perm)) continue;
			
			String name = creator.getName();
			validCreators.add(name);
		}
		return validCreators;
	}
	
	public MobCreator<?> tryGetCreator(String name) {
		return creators.get(name);
	}
	
	public MobCreator<?> getCreator(String name) {
		checkNotNull(name, "Name must not be null.");
		checkArgument(creators.containsKey(name), "Name must be a valid creator name (got '%s').", name);
		return creators.get(name);
	}
	
	public boolean isValid(String name) {
		return creators.containsKey(name);
	}
}
