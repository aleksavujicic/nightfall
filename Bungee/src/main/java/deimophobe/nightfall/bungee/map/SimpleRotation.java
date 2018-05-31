package deimophobe.nightfall.bungee.map;

import deimophobe.nightfall.bungee.util.Util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Deimophobe on 19/05/18.
 */
public class SimpleRotation implements Rotation {
	private final Set<GameMap> maps;
	private final String name;
	
	public SimpleRotation(String name, Collection<GameMap> maps) {
		checkNotNull(maps, "Set of game maps for simple rotation must not be null.");
		
		this.name = name;
		this.maps = new HashSet<>(maps);
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public GameMap getMap() {
		return Util.getRandom(maps);
	}
}
