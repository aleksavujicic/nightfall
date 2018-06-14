package deimophobe.nightfall.status;

import deimophobe.nightfall.game.GameEntity;

import java.util.Set;

/**
 * Created by Deimophobe on 11/06/18.
 */
public interface StatusEffectApplier<T> {
	void setState(GameEntity<?> receiver, Set<T> levels, int duration);
	void update();
}
