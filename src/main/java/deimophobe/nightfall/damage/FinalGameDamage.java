package deimophobe.nightfall.damage;

import deimophobe.nightfall.entity.GameEntity;

/**
 * Created by Deimophobe on 14/02/18.
 */
public interface FinalGameDamage<A extends GameEntity, R extends GameEntity> {
	A getAttacker();
	R getReceiver();
	GameDamageType getType();
	
	double getFinalDamage();
	boolean willKill();
}
