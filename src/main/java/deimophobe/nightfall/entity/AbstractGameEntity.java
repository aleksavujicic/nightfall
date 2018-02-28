package deimophobe.nightfall.entity;

import deimophobe.nightfall.Game;
import deimophobe.nightfall.damage.DamageOverTimeType;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Deimophobe on 28/02/18.
 */
public class AbstractGameEntity<T extends LivingEntity> implements GameEntity<T> {
	
	protected final T entity;
	@Override public T getEntity() { return entity; }
	
	private final Map<DamageOverTimeType, Long> lastDamageTicks = new HashMap<>();
	
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
		lastDamageTicks.putIfAbsent(type, 0L);
		return lastDamageTicks.get(type) + requiredDelay <= Game.getGame().getCurrentTick();
	}
}
