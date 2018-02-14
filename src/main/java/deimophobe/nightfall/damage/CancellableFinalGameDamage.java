package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GameEntity;

/**
 * Created by Deimophobe on 14/02/18.
 */
public interface CancellableFinalGameDamage<A extends GameEntity, R extends GameEntity> extends FinalGameDamage<A, R> {
	void cancel();
	void softCancel();
}
