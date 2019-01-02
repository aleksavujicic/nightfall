package deimophobe.nightfall.game;

import deimophobe.nightfall.damage.dot.DamageOverTimeType;
import deimophobe.nightfall.game.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 28/02/18.
 */
public abstract class AbstractGameEntity<T extends LivingEntity> implements GameEntity<T> {
	
	protected T entity;
	@Override public T getEntity() { return entity; }
	
	private final Map<DamageOverTimeType, Integer> lastDamageTicks = new EnumMap<>(DamageOverTimeType.class);
	
	public AbstractGameEntity(T entity) {
		this.entity = entity;
	}
	
	public AbstractGameEntity(Location spawnLocation, Class<T> entityClass, Consumer<T> spawner) {
		this.entity = spawnLocation.getWorld().spawn(spawnLocation, entityClass, spawner::accept);
	}
	
	@Override
	public void doDamageOverTimeTick(DamageOverTimeType type) {
		lastDamageTicks.put(type, Game.getGame().getCurrentTick());
	}
	
	@Override
	public boolean canDamageOverTimeTick(DamageOverTimeType type, long requiredDelay) {
		lastDamageTicks.putIfAbsent(type, 0);
		return lastDamageTicks.get(type) + requiredDelay <= Game.getGame().getCurrentTick();
	}
}
