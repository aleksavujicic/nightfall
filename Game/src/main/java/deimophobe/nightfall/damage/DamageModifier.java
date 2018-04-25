package deimophobe.nightfall.damage;

import deimophobe.nightfall.game.GameEntity;

/**
 * Created by Deimophobe on 26/04/18.
 */
@FunctionalInterface
public interface DamageModifier<A extends GameEntity,R extends GameEntity> {
	void modifyDamage(GameDamage<A,R> gameDamage);
}
