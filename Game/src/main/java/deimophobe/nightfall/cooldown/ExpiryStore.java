package deimophobe.nightfall.cooldown;

import deimophobe.nightfall.game.Game;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Deimophobe on 6/06/18.
 */
public class ExpiryStore<I> {
	private final Game game;
	private final Map<I, Integer> expiryMap = new HashMap<>();
	
	public ExpiryStore() {
		this.game = Game.getGame();
	}
	
	public ExpiryStore(Game game) {
		this.game = game;
	}
	
	public void addItem(I item, int duration) {
		checkArgument(duration >= 0, "Duration must be non-negative.");
		if (duration == 0) return;
		
		ensureExistence(item);
		//noinspection ConstantConditions
		expiryMap.compute(item, (k, oldTime) -> Math.max(oldTime, getCurrentTime() + duration));
	}
	
	public boolean hasExpired(I item) {
		ensureExistence(item);
		return expiryMap.get(item) <= getCurrentTime();
	}
	
	private void ensureExistence(I item) {
		expiryMap.putIfAbsent(item, 0);
	}
	
	private int getCurrentTime() {
		return game.getCurrentTick();
	}
}
