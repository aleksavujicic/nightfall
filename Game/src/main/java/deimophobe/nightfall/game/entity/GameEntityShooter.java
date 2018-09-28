package deimophobe.nightfall.game.entity;

import org.bukkit.entity.LivingEntity;

/**
 * Created by Deimophobe on 29/09/18.
 */
public interface GameEntityShooter<E extends LivingEntity> extends GameEntity<E>, GameShooter {
}
